package com.example.todolistapp.SignUpIn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.todolistapp.R
import com.example.todolistapp.databinding.ActivitySignInUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction().replace(R.id.signInUpFragmentContainer, SignInFragment(this)).commit()
    }
}