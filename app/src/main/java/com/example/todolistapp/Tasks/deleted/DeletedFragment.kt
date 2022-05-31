package com.example.todolistapp.Tasks.deleted

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistapp.MainActivity
import com.example.todolistapp.R
import com.example.todolistapp.Tasks.Task
import com.example.todolistapp.Tasks.activeTasks.ActiveTasksRecyclerViewAdapter
import com.example.todolistapp.Tasks.createTask.CreateTaskFragment
import com.example.todolistapp.databinding.FragmentDeletedBinding
import com.example.todolistapp.databinding.FragmentTodayBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DeletedFragment(private val act: MainActivity) : Fragment(), DeletedFragmentRecyclerViewAdapter.OnItemClickListener  {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: FragmentDeletedBinding
    private lateinit var deletedTasksArrayList: ArrayList<Task>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeletedBinding.inflate(layoutInflater, container, false)
        auth = Firebase.auth
        deletedTasksArrayList = arrayListOf()
        databaseReference = FirebaseDatabase.getInstance().getReference("Tasks/${auth.currentUser?.uid}")
        binding.recyclerViewActiveTasksFragment.layoutManager = LinearLayoutManager(act)
        Log.d("MyTag", "onCreateView: ${getActiveTasksFromDatabase().size}")
        // Inflate the layout for this fragment
        return binding.root
    }

    fun getActiveTasksFromDatabase() : ArrayList<Task>{
        databaseReference.addValueEventListener(object: ValueEventListener {
            val currentDateTimeInMillis = System.currentTimeMillis()
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    deletedTasksArrayList.clear()
                    for(taskSnapshot in snapshot.children){
                        val task = taskSnapshot.getValue(Task::class.java)!!
//                        tasksArrayList.add(task)
//                        Log.d("MyTag", "onDataChange: ${task.title}")
                        if(task.isDone == true && task.isDeleted == false){
                            deletedTasksArrayList.add(task)
                            Log.d("MyTag", "onDataChange: ${task.title}")
                        }
                    }
                    binding.recyclerViewActiveTasksFragment.adapter = DeletedFragmentRecyclerViewAdapter(deletedTasksArrayList, this@DeletedFragment)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("MyTag", "onCancelled: DatabaseError: $error")
            }
        })
        Log.d("MyTag", "getActiveTasksFromDatabase: ${deletedTasksArrayList.size}")
        return deletedTasksArrayList
    }

    override fun onItemClick(position: Int) {
        act.supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerToolbar, CreateTaskFragment(act, deletedTasksArrayList[position])).addToBackStack(null).commit()
    }

    override fun onBtnDeleteClick(position: Int) {
        val tempTask = deletedTasksArrayList[position]
        tempTask.isDeleted = true
        updateTask(tempTask, position)
    }

    override fun onBtnDoneClick(position: Int) {
        val tempTask = deletedTasksArrayList[position]
        val year = SimpleDateFormat("yyyy").format(System.currentTimeMillis()).toInt()
        val month = SimpleDateFormat("MM").format(System.currentTimeMillis()).toInt()
        val day = SimpleDateFormat("dd").format(System.currentTimeMillis()).toInt()
        val hour = SimpleDateFormat("HH").format(System.currentTimeMillis()).toInt()
        val minute = SimpleDateFormat("mm").format(System.currentTimeMillis()).toInt()

        val date = convertDateTimeToMillis(year, month, day+1, hour, minute)

        tempTask.dateTimeInMillis = date
        tempTask.isDone = false
        updateTask(tempTask, position)
    }

    private fun updateTask(tempTask: Task, position:Int){
        deletedTasksArrayList.removeAt(position)
        binding.recyclerViewActiveTasksFragment.adapter?.notifyDataSetChanged()
        databaseReference.child(tempTask.taskId!!).setValue(tempTask).addOnSuccessListener{
            Toast.makeText(act, "Successfully removed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertDateTimeToMillis(year:Int, month:Int, day: Int, hour: Int, minute: Int):Long{
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertMillisToDateTime(millis: Long): ArrayList<Int>{
        val year = SimpleDateFormat("yyyy").format(millis).toInt()
        val month = SimpleDateFormat("MM").format(millis).toInt()
        val day = SimpleDateFormat("dd").format(millis).toInt()
        val hour = SimpleDateFormat("HH").format(millis).toInt()
        val minute = SimpleDateFormat("mm").format(millis).toInt()
        val arrayListOfDate = arrayListOf<Int>()
        arrayListOfDate.add(year)
        arrayListOfDate.add(month)
        arrayListOfDate.add(day)
        arrayListOfDate.add(hour)
        arrayListOfDate.add(minute)
//        Log.d("MyTag", "convertMillisToDateTime: $day/$month/$year  $hour/$minute")
        return arrayListOfDate
    }

}