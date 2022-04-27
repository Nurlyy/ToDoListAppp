package com.example.todolistapp.SignUpIn

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.example.todolistapp.MainActivity
import com.example.todolistapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignInHelper(private val auth: FirebaseAuth, private val act: AppCompatActivity) {
    fun signInWithEmail(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            Toast.makeText(act, "Welcome!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(act, "Can not sign in", Toast.LENGTH_SHORT).show()
        }
    }

    fun forgotPasswordClicked(email: String){
        auth.sendPasswordResetEmail(email)
    }

    fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        return GoogleSignIn.getClient(act, gso)
    }

    fun signInWithGoogle(launcher: ActivityResultLauncher<Intent>){
        val googleSignInClient = getSignInClient()
        launcher.launch(googleSignInClient.signInIntent)
    }

    fun firebaseAuthWithGoogle(idToken:String){
        Log.d("MyTag", "firebaseAuthWithGoogle: first pass")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener{
            Log.d("MyTag", "firebaseAuthWithGoogle: second pass")
            if(it.isSuccessful){
                Toast.makeText(act, "Welcome!", Toast.LENGTH_SHORT).show()
                act.startActivity(Intent(act, MainActivity()::class.java))
                act.finish()
            }
            else{
                Toast.makeText(act, "Error Signing In", Toast.LENGTH_SHORT).show()
            }
        }
    }
}