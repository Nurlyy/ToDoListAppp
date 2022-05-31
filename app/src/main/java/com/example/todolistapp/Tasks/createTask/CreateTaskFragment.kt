package com.example.todolistapp.Tasks.createTask

import android.app.Activity
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.todolistapp.MainActivity
import com.example.todolistapp.R
import com.example.todolistapp.Tasks.Task
import com.example.todolistapp.databinding.FragmentCreateTaskBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class CreateTaskFragment(private val act: MainActivity, private val task: Task?) : Fragment() {
    private lateinit var binding: FragmentCreateTaskBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var createTaskHelper: CreateTaskHelper
    private lateinit var storage: FirebaseStorage
    private var isFavoriteClicked = false
    private var pickFileLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent(), ActivityResultCallback {
            if(it!=null){
                createTaskHelper.uploadFileToDatabase(storage, it, act.findViewById(R.id.buttonSaveCreateTaskFragment))
            }
        }
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        auth = Firebase.auth
        storage = FirebaseStorage.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Tasks/${auth.currentUser?.uid}")
        binding = FragmentCreateTaskBinding.inflate(inflater, container, false)
        createTaskHelper = CreateTaskHelper(act, auth, databaseReference, binding)
        createTaskHelper.displayInformationFromTask(if(task!=null){task}else{null})

        binding.editTextTitleCreateTaskFragment

        binding.imageViewFavorite.setOnClickListener{
            isFavoriteClicked = !isFavoriteClicked
            if(isFavoriteClicked){
                binding.imageViewFavorite.setImageResource(R.drawable.ic_favorite)
            }else{
                binding.imageViewFavorite.setImageResource(R.drawable.ic_favorite_border)
            }
        }

        binding.cardViewPickFileCreateTaskFragment.setOnClickListener{
            if(createTaskHelper.selectedFileUri==null)
                pickFileLauncher.launch("*/*")
        }
        binding.buttonSaveCreateTaskFragment.setOnClickListener {
            createTaskHelper.createUpdateTask(if(task!=null){task}else{null}, isFavoriteClicked)
        }
        binding.imageViewDeleteFileCreateTaskFragment.setOnClickListener {
            binding.linearLayoutUriCreateTaskFragment.visibility = View.GONE
            binding.textViewPickFileCreateTaskFragment.visibility = View.VISIBLE
            createTaskHelper.isRemoveSelectedFileClicked = true
        }

        return binding.root
    }

}