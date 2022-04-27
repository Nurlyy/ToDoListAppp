package com.example.todolistapp.Profile

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.todolistapp.FirstScreenActivity
import com.example.todolistapp.MainActivity
import com.example.todolistapp.R
import com.example.todolistapp.databinding.FragmentProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class ProfileFragment(val act: MainActivity) : Fragment() {
    private lateinit var auth : FirebaseAuth
    private lateinit var binding: FragmentProfileBinding
    private lateinit var imageUri: Uri
    private val DEFAULT_IMAGE_URL = "https://pic.onlinewebfonts.com/svg/img_458488.png"
    @SuppressLint("NewApi")
    private val changeImage = registerForActivityResult(
        ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            if(it!=null){
                val source = ImageDecoder.createSource(act.contentResolver, it)
                val bitmap = ImageDecoder.decodeBitmap(source)
                binding.buttonProfileFragmentSaveChanges.isEnabled = true
                uploadImageAndSaveUri(bitmap)
            }
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        setProfileInformationOnCreate(binding.editTextProfileFragmentEmail, binding.editTextProfileFragmentUsername)

        binding.imageViewProfileFragmentProfileImage.setOnClickListener{
            changeImageOnClick()
        }

        binding.editTextProfileFragmentUsername.addTextChangedListener {
            binding.buttonProfileFragmentSaveChanges.isEnabled = true
        }

        binding.buttonProfileFragmentSaveChanges.setOnClickListener{
            saveChangesClicked(binding.editTextProfileFragmentUsername.text.toString())
        }

        binding.textViewProfileFragmentLogOut.setOnClickListener{
            logOutFromAccount()
        }
        return binding.root
    }

    private fun uploadImageAndSaveUri(imageBitmap: Bitmap){
        val baos = ByteArrayOutputStream()
        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("pics/${auth.currentUser?.uid}")
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val imageByteArray = baos.toByteArray()
        val upload = storageRef.putBytes(imageByteArray).addOnCompleteListener{
            if(it.isSuccessful){
                storageRef.downloadUrl.addOnCompleteListener{urlTask->
                    urlTask.result.let {uri->
                        imageUri = uri
                        binding.imageViewProfileFragmentProfileImage.setImageBitmap(imageBitmap)
                        act.updateUi(imageBitmap)
                    }
                }
            }
            else{
                Log.d("MyTag", "uploadImageAndSaveUri: ${it.exception}")
            }
        }
    }

    fun changeImageOnClick(){
        changeImage.launch("image/*")
    }

    fun setProfileInformationOnCreate(email: EditText, username: EditText){
        val account = auth.currentUser
        if(account?.photoUrl!=null){
            Picasso.get().load(account.photoUrl).into(binding.imageViewProfileFragmentProfileImage)
        }
        else{
            Picasso.get().load(DEFAULT_IMAGE_URL).into(binding.imageViewProfileFragmentProfileImage)
        }

        email.text.clear()
        email.setText(account?.email)

        if(account?.displayName!=null){
            username.text.clear()
            username.setText(account.displayName)
        }
    }

    fun saveChangesClicked(username:String){
        val photo = when {
            ::imageUri.isInitialized -> imageUri
            auth.currentUser?.photoUrl==null -> Uri.parse(DEFAULT_IMAGE_URL)
            else -> auth.currentUser?.photoUrl
        }
        val updates = UserProfileChangeRequest.Builder()
            .setDisplayName(username)
            .setPhotoUri(photo)
            .build()

        auth.currentUser?.updateProfile(updates)?.addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(act, "User updated", Toast.LENGTH_SHORT).show()
                binding.buttonProfileFragmentSaveChanges.isEnabled = false
            }
            else{
                Toast.makeText(act, "Something went wrong...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getSignInClient(): GoogleSignInClient {
        val temp = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(act, temp)
    }

    fun logOutFromAccount(){
        val signInClient = getSignInClient()
        auth.signOut()
        signInClient.signOut().addOnCompleteListener {
            act.startActivity(Intent(act, FirstScreenActivity::class.java))
            act.finish()
        }
    }

}