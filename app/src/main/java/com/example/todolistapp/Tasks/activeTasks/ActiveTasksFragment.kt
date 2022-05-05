package com.example.todolistapp.Tasks.activeTasks

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.MainActivity
import com.example.todolistapp.R
import com.example.todolistapp.Tasks.Task
import com.example.todolistapp.Tasks.createTask.CreateTaskFragment
import com.example.todolistapp.databinding.FragmentActiveTasksBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class ActiveTasksFragment(private val act: MainActivity) : Fragment(), ActiveTasksRecyclerViewAdapter.OnItemClickListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentActiveTasksBinding
    private lateinit var databaseReference: DatabaseReference
    private var activeTasksArrayList = arrayListOf<Task>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentActiveTasksBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        databaseReference = FirebaseDatabase.getInstance().getReference("Tasks/${auth.currentUser?.uid}")
        binding.recyclerViewActiveTasksFragment.layoutManager = LinearLayoutManager(act)
        Log.d("MyTag", "onCreateView: ${getActiveTasksFromDatabase().size}")
        binding.floatingActionButtonActiveTasksFragment.setOnClickListener{
            act.supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerToolbar, CreateTaskFragment(act, null)).commit()
        }
        return binding.root
    }


    fun getActiveTasksFromDatabase() : ArrayList<Task>{

        databaseReference.addValueEventListener(object: ValueEventListener {
            val currentDateTimeInMillis = System.currentTimeMillis()
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    activeTasksArrayList.clear()
                    for(taskSnapshot in snapshot.children){
                        val task = taskSnapshot.getValue(Task::class.java)!!
//                        tasksArrayList.add(task)
//                        Log.d("MyTag", "onDataChange: ${task.title}")
                        if(task.isDeleted == false && task.isDone == false){
                            if(task.dateTimeInMillis>=currentDateTimeInMillis){
                                activeTasksArrayList.add(task)
                                Log.d("MyTag", "onDataChange: ${task.title}")
                            }
                            else{
                                task.isDone = true
                                databaseReference.child(task.taskId!!).setValue(task).addOnSuccessListener{
                                    Toast.makeText(act, "One task is done", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    binding.recyclerViewActiveTasksFragment.adapter = ActiveTasksRecyclerViewAdapter(activeTasksArrayList, this@ActiveTasksFragment)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("MyTag", "onCancelled: DatabaseError: $error")
            }
        })
        Log.d("MyTag", "getActiveTasksFromDatabase: ${activeTasksArrayList.size}")
        return activeTasksArrayList
    }


    override fun onItemClick(position: Int) {
        act.supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerToolbar, CreateTaskFragment(act, activeTasksArrayList[position])).addToBackStack(null).commit()
    }

    override fun onBtnDeleteClick(position: Int) {
        val tempTask = activeTasksArrayList[position]
        tempTask.isDone = true
        updateTask(tempTask, position)
    }

    override fun onBtnDoneClick(position: Int) {
        val tempTask = activeTasksArrayList[position]
        tempTask.isDone = true
        updateTask(tempTask, position)
    }

    private fun updateTask(tempTask: Task, position:Int){
        activeTasksArrayList.removeAt(position)
        binding.recyclerViewActiveTasksFragment.adapter?.notifyDataSetChanged()
        databaseReference.child(tempTask.taskId!!).setValue(tempTask).addOnSuccessListener{
            Toast.makeText(act, "Successfully removed", Toast.LENGTH_SHORT).show()
        }
    }

}