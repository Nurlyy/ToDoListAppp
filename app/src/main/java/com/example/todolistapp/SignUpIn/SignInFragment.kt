package com.example.todolistapp.SignUpIn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.todolistapp.MainActivity
import com.example.todolistapp.R
import com.example.todolistapp.databinding.FragmentSignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInFragment(private val act: AppCompatActivity) : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var signInHelper : SignInHelper
    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        auth = Firebase.auth
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        signInHelper = SignInHelper(auth, act)
        binding.tvDontHaveAnAccount.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.signInUpFragmentContainer, SignUpFragment(act)).commit()
        }
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try{
                val account = task.getResult(ApiException::class.java)
                if(account!=null){
                    signInHelper.firebaseAuthWithGoogle(account.idToken!!)
                }
            }catch (e: ApiException){
                Log.d("MyTag", "onCreate: ${e}")
            }
        }
        binding.buttonSignIn.setOnClickListener {
            with(binding){
                signInHelper.signInWithEmail(editTextSignInEmail.text.toString(), editTextSignInPassword.text.toString())
            }
            act.startActivity(Intent(act, MainActivity::class.java))
            act.finish()
        }
        binding.buttonSignInWithGoggle.setOnClickListener {
            signInHelper.signInWithGoogle(launcher)
        }

        binding.tvForgotPassword.setOnClickListener{
            if(binding.editTextSignInEmail.text.isNotEmpty())
                signInHelper.forgotPasswordClicked(binding.editTextSignInEmail.text.toString())
        }
        return binding.root
    }

}