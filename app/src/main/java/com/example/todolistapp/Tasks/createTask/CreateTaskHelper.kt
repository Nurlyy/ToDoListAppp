package com.example.todolistapp.Tasks.createTask

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.todolistapp.MainActivity
import com.example.todolistapp.R
import com.example.todolistapp.Tasks.Task
import com.example.todolistapp.Tasks.activeTasks.ActiveTasksFragment
import com.example.todolistapp.databinding.FragmentCreateTaskBinding
import com.example.todolistapp.notification.NOTIFICATION_ID
import com.example.todolistapp.notification.NotificationHelper
import com.example.todolistapp.notification.messageExtra
import com.example.todolistapp.notification.titleExtra
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CreateTaskHelper(private val act: MainActivity, private val auth: FirebaseAuth, private val databaseReference: DatabaseReference, private val binding: FragmentCreateTaskBinding) {

    //selectAndUploadFile Variables
    var selectedFileUri:String? = null
    var isRemoveSelectedFileClicked = false

    //notification variables
    private var notificationID:Int = 0

    //DateTime Variables
    private var selectedDateTimeInMillis : Long = 0
    private var currentDateTimeInMillis: Long = 0
    private var selectedYear=0
    private var selectedMonth=0
    private var selectedDay=0
    private var selectedHour =0
    private var selectedMinute=0


    private var tasksArrayList = arrayListOf<Task>()




    //-----------------FUNCTIONS-------------------------



    //---1---Display-information-from-selected-task

    fun displayInformationFromTask(task: Task?){
        if(task!=null){
            binding.editTextTitleCreateTaskFragment.setText(task.title)
            binding.editTextDescriptionCreateTaskFragment.setText(task.description)
            val listOfDateTime = convertMillisToDateTime(task.dateTimeInMillis)
            binding.datePicker.init(listOfDateTime[0], listOfDateTime[1]-1, listOfDateTime[2], DatePicker.OnDateChangedListener{datePicker, i, i2, i3 ->})
            binding.timePicker.hour = listOfDateTime[3]
            binding.timePicker.minute = listOfDateTime[4]
            if(task.isFavorite){
                binding.imageViewFavorite.setImageResource(R.drawable.ic_favorite)
            }else{
                binding.imageViewFavorite.setImageResource(R.drawable.ic_favorite_border)
            }
            binding.checkBoxNotificationCreateTaskFragment.isChecked = task.notification
            if(task.file!=null){
                selectedFileUri = task.file
                binding.textViewSelectedFileUriCreateTaskFragment.text = task.file
                binding.linearLayoutUriCreateTaskFragment.visibility = View.VISIBLE
                binding.textViewPickFileCreateTaskFragment.visibility = View.GONE
            }else{
                binding.linearLayoutUriCreateTaskFragment.visibility = View.GONE
                binding.textViewPickFileCreateTaskFragment.visibility = View.VISIBLE
            }
        }
    }






    //---2---Create-or-update-task

    //---2.1---Create-task-object-from-entered-data
    fun createUpdateTask(task: Task?, isFavoriteClicked: Boolean){
        if(binding.editTextTitleCreateTaskFragment.text.isNotEmpty()){
            val taskId = if(task==null){databaseReference.push().key}else{task.taskId}
            getDateTime(binding.datePicker, binding.timePicker)
            notificationID = if(task==null){createNotificationId()}else{task.notificationID!!}
            selectedDateTimeInMillis = convertDateTimeToMillis(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute)
            currentDateTimeInMillis = System.currentTimeMillis()
            if(isRemoveSelectedFileClicked == true){
                FirebaseStorage.getInstance().getReferenceFromUrl(selectedFileUri!!).delete()
                selectedFileUri = null
                task?.file=null
            }
            if(binding.checkBoxNotificationCreateTaskFragment.isChecked){
                scheduleNotification(binding.editTextTitleCreateTaskFragment.text.toString(),
                    binding.editTextDescriptionCreateTaskFragment.text.toString(), selectedDateTimeInMillis,
                    notificationID, act.applicationContext)
            }else{
                cancelNotification(notificationID, act.applicationContext)
            }
            if(selectedDateTimeInMillis>=currentDateTimeInMillis) {
                val tempTask = Task(
                    taskId, binding.editTextTitleCreateTaskFragment.text.toString(),
                    binding.editTextDescriptionCreateTaskFragment.text.toString(), selectedDateTimeInMillis,
                    binding.checkBoxNotificationCreateTaskFragment.isChecked, notificationID,
                    selectedFileUri
                )
                tempTask.isFavorite = isFavoriteClicked
                Log.d("MyTag", "createUpdateTask: PASSED!")
                uploadTaskToDatabase(tempTask)
            }else{
                Toast.makeText(act, "Please select correct date and time", Toast.LENGTH_SHORT).show()
                Log.d("MyTag", "selected datetime: $selectedDay/$selectedMonth/$selectedYear   $selectedHour:$selectedMinute")
            }
        }
        else{
            Toast.makeText(act, "Please enter at least title of task", Toast.LENGTH_SHORT).show()
        }
    }

    //---2.2---Upload-task-object-to-firebase
    private fun uploadTaskToDatabase(task: Task){
        databaseReference.child(task.taskId.toString()).setValue(task).addOnSuccessListener {
            Toast.makeText(act, "Task saved", Toast.LENGTH_SHORT).show()
            act.supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerToolbar, ActiveTasksFragment(act)).commit()
        }.addOnFailureListener{
            Toast.makeText(act, "Something went wrong", Toast.LENGTH_SHORT).show()
            Log.d("MyTag", "uploadTaskToDatabase: ${it.message}")
            act.supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerToolbar, ActiveTasksFragment(act)).commit()
        }
    }

    fun uploadFileToDatabase(storage: FirebaseStorage, uri: Uri, button: Button){
        val tempStorage = storage.getReference("taskFiles/${auth.currentUser?.uid}")
            .child("${uri.lastPathSegment}")
        button.isEnabled = false
        button.isClickable= false
        tempStorage.putFile(uri).addOnSuccessListener{
            tempStorage.downloadUrl.addOnSuccessListener{uri->
                button.isEnabled = true
                button.isClickable=true
                binding.textViewSelectedFileUriCreateTaskFragment.text = uri.toString()
                binding.linearLayoutUriCreateTaskFragment.visibility = View.VISIBLE
                binding.textViewPickFileCreateTaskFragment.visibility = View.GONE
                isRemoveSelectedFileClicked = false
                selectedFileUri = uri.toString()
                Toast.makeText(act, "File chosen", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Log.d("MyTag", "uploadFileToDatabase: Failed! ${it.message}")
        }
    }





    //---3---Date-time-functions

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

    private fun getDateTime(datePicker: DatePicker, timePicker: TimePicker){
        selectedYear = datePicker.year
        selectedMonth = datePicker.month
        selectedDay = datePicker.dayOfMonth
        selectedHour = timePicker.hour
        selectedMinute = timePicker.minute
    }






    //---4---Notifications-------

    private fun createNotificationId(): Int{
        return Random().nextInt(1000000000)
    }


    fun scheduleNotification(title:String, message:String, time:Long, notificationID:Int, context:Context){
        val intent = Intent(context, NotificationHelper::class.java)
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)
        intent.putExtra(NOTIFICATION_ID, notificationID)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        Log.d("MyTag", "scheduleNotification: SCHEDULED")
    }

    fun cancelNotification(notificationID: Int, context: Context){
        val intent = Intent(context, NotificationHelper::class.java)
            .putExtra(NOTIFICATION_ID, notificationID)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationID,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }

}