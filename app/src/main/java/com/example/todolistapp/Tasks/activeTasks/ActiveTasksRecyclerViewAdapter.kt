package com.example.todolistapp.Tasks.activeTasks

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.R
import com.example.todolistapp.Tasks.Task
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ActiveTasksRecyclerViewAdapter(val sortedActiveTasks: ArrayList<Task>, val listener: OnItemClickListener): RecyclerView.Adapter<ActiveTasksRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveTasksRecyclerViewAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.active_task_recycler_view_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ActiveTasksRecyclerViewAdapter.ViewHolder, position: Int) {
        val task = sortedActiveTasks[position]
        val selectedDateTimeArrayList : ArrayList<Int> = convertMillisToDateTime(task.dateTimeInMillis)
        holder.textViewTitle.text = task.title
        holder.textViewDescription.text = task.description
        holder.textViewDate.text = "${selectedDateTimeArrayList[0]}/${selectedDateTimeArrayList[1]}/${selectedDateTimeArrayList[2]}"
    }

    override fun getItemCount(): Int {
        Log.d("MyTag", "getItemCount: ${sortedActiveTasks.size}")
        return sortedActiveTasks.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var textViewTitle: TextView
        var textViewDescription: TextView
        var textViewDate: TextView
        var buttonDelete: Button
        var buttonDone: Button

        init {
            textViewTitle = itemView.findViewById(R.id.textViewTitleActiveTaskRecyclerView)
            textViewDescription = itemView.findViewById(R.id.textViewDescriptionActiveTaskRecyclerView)
            textViewDate = itemView.findViewById(R.id.textViewDateActiveTasksRecyclerView)
            buttonDelete = itemView.findViewById(R.id.buttonDeleteActiveTaskRecyclerView)
            buttonDone = itemView.findViewById(R.id.buttonDoneActiveTaskRecyclerView)

            textViewTitle.setOnClickListener(this)
            textViewDescription.setOnClickListener(this)
            textViewDate.setOnClickListener(this)
            buttonDelete.setOnClickListener(this)
            buttonDone.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            val position = adapterPosition

            if(position!=RecyclerView.NO_POSITION){
                if(view?.id == textViewTitle.id || view?.id == textViewDescription.id || view?.id == textViewDate.id){
                    listener.onItemClick(position)
                }
                else if(view?.id == buttonDelete.id){
                    listener.onBtnDeleteClick(position)
                }
                else if(view?.id == buttonDone.id){
                    listener.onBtnDoneClick(position)
                }
            }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onBtnDeleteClick(position: Int)
        fun onBtnDoneClick(position: Int)
    }




    //--- ---Date-time-functions

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