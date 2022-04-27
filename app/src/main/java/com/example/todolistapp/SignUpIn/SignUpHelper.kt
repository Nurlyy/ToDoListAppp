package com.example.todolistapp.SignUpIn

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todolistapp.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class SignUpHelper(private val act: AppCompatActivity, private val auth: FirebaseAuth) {
    fun signUp(email: String, password: String, username: String){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                sendEmailVerification(it.result.user!!)
                val updates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()
                it.result.user!!.updateProfile(updates)
            }else{
                Toast.makeText(act, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun sendEmailVerification(user: FirebaseUser){
        user.sendEmailVerification()
        Toast.makeText(act, "Signed Up! Verification Email was sent!", Toast.LENGTH_SHORT).show()
    }
}