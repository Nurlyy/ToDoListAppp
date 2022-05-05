package com.example.todolistapp.Tasks.activeTasks

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.MainActivity
import com.example.todolistapp.R
import com.example.todolistapp.Tasks.Task
import com.example.todolistapp.Tasks.createTask.CreateTaskFragment
import com.example.todolistapp.databinding.FragmentActiveTasksBinding
import com.example.todolistapp.notification.NOTIFICATION_ID
import com.example.todolistapp.notification.NotificationHelper
import com.example.todolistapp.notification.messageExtra
import com.example.todolistapp.notification.titleExtra
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ActiveTasksHelper(
    private val act: MainActivity) {


    fun scheduleNotification(title:String, message:String, time:Long, notificationID:Long){
        val intent = Intent(act, NotificationHelper::class.java)
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)
        intent.putExtra(NOTIFICATION_ID, notificationID.toString())
        val pendingIntent = PendingIntent.getBroadcast(
            act,
            notificationID.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = act.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    fun cancelNotification(notificationID: Long){
        val intent = Intent(act, NotificationHelper::class.java)
            .putExtra(NOTIFICATION_ID, notificationID.toString())
        val alarmManager = act.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            act,
            notificationID.toInt(),
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }




//    fun getActiveTasksFromDatabase() : ArrayList<Task>{
//
//        databaseReference.addValueEventListener(object: ValueEventListener {
//            val currentDateTimeInMillis = System.currentTimeMillis()
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if(snapshot.exists()){
//                    activeTasksArrayList.clear()
//                    for(taskSnapshot in snapshot.children){
//                        val task = taskSnapshot.getValue(Task::class.java)!!
////                        tasksArrayList.add(task)
////                        Log.d("MyTag", "onDataChange: ${task.title}")
//                        if(task.isDeleted == false && task.isDone == false){
//                            if(task.dateTimeInMillis>=currentDateTimeInMillis){
//                                activeTasksArrayList.add(task)
//                                Log.d("MyTag", "onDataChange: ${task.title}")
//                            }
//                            else{
//                                task.isDone = true
//                                databaseReference.child(task.taskId!!).setValue(task).addOnSuccessListener{
//                                    Toast.makeText(act, "One task is done", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//                        }
//                    }
//                    binding.recyclerViewActiveTasksFragment.adapter = ActiveTasksRecyclerViewAdapter(activeTasksArrayList, ActiveTasksFragment(act))
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("MyTag", "onCancelled: DatabaseError: $error")
//            }
//        })
//        Log.d("MyTag", "getActiveTasksFromDatabase: ${activeTasksArrayList.size}")
//        return activeTasksArrayList
//    }
//
//    fun onItemClick(position: Int){
//        act.supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerToolbar, CreateTaskFragment(act, activeTasksArrayList[position]))
//    }
//
//    fun onBtnDoneClick(position: Int) {
//        val tempTask = activeTasksArrayList[position]
//        tempTask.isDone = true
//        updateTask(tempTask, position)
//    }
//
//    fun onBtnDeleteClick(position: Int) {
//        var tempTask = activeTasksArrayList[position]
//        tempTask.isDeleted = true
//        updateTask(tempTask, position)
//    }
//
//    fun updateTask(tempTask: Task, position:Int){
//        activeTasksArrayList.removeAt(position)
//        binding.recyclerViewActiveTasksFragment.adapter?.notifyDataSetChanged()
//        databaseReference.child(tempTask.taskId!!).setValue(tempTask).addOnSuccessListener{
//            Toast.makeText(act, "Successfully removed", Toast.LENGTH_SHORT).show()
//        }
//    }

}