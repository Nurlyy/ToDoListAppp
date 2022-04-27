package com.example.todolistapp.SignUpIn

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todolistapp.MainActivity
import com.example.todolistapp.R
import com.example.todolistapp.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpFragment(private val act: AppCompatActivity) : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var signUpHelper: SignUpHelper
    private lateinit var auth : FirebaseAuth
    private lateinit var etEmail: EditText
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSignUp: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        binding.tvAlreadySignedUp.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.signInUpFragmentContainer, SignInFragment(act)).commit()
        }
        signUpHelper = SignUpHelper(act, auth)
        etEmail = binding.editTextSignUpEmail
        etUsername = binding.editTextSignUpUsername
        etPassword = binding.editTextSignUpPassword
        etConfirmPassword = binding.editTextSignUpConfirmPassword
        btnSignUp = binding.buttonSignUp

        btnSignUp.setOnClickListener{
            btnSignUpClicked()
        }
        return binding.root
    }

    private fun btnSignUpClicked(){
        if(etEmail.text.isNotEmpty() && etUsername.text.isNotEmpty() && etPassword.text.isNotEmpty() && etConfirmPassword.text.isNotEmpty()){
            if(etPassword.text.toString() == etConfirmPassword.text.toString()){
                signUpHelper.signUp(etEmail.text.toString(), etPassword.text.toString(), etUsername.text.toString())
                act.startActivity(Intent(act, MainActivity::class.java))
                act.finish()
            }
            else{
                Toast.makeText(act, "Passwords does not match", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(act, "Please fill all the fields!", Toast.LENGTH_SHORT).show()
        }
    }

}