package com.example.todolistapp.Tasks.favorite

import android.annotation.SuppressLint
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.R
import com.example.todolistapp.Tasks.Task
import com.example.todolistapp.Tasks.deleted.DeletedFragmentRecyclerViewAdapter
import java.text.SimpleDateFormat

class FavoriteFragmentRecyclerViewAdapter(val sortedActiveTasks: ArrayList<Task>, private val listener: FavoriteFragmentRecyclerViewAdapter.OnItemClickListener): RecyclerView.Adapter<FavoriteFragmentRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.favorite_fragment_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = sortedActiveTasks[position]
        val selectedDateTimeArrayList : ArrayList<Int> = convertMillisToDateTime(task.dateTimeInMillis)
        holder.textViewTitle.text = task.title
        holder.textViewDescription.text = task.description
        holder.textViewDate.text = "${selectedDateTimeArrayList[0]}/${selectedDateTimeArrayList[1]}/${selectedDateTimeArrayList[2]}"

    }

    override fun getItemCount(): Int {
        return sortedActiveTasks.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var textViewTitle: TextView
        var textViewDescription: TextView
        var textViewDate: TextView
        var buttonDelete: Button
        var buttonDone: Button

        init {
            textViewTitle = itemView.findViewById(R.id.textViewTitleFavoriteTaskRecyclerView)
            textViewDescription = itemView.findViewById(R.id.textViewDescriptionFavoriteTaskRecyclerView)
            textViewDate = itemView.findViewById(R.id.textViewDateFavoriteTasksRecyclerView)
            buttonDelete = itemView.findViewById(R.id.buttonDeleteFavoriteTaskRecyclerView)
            buttonDone = itemView.findViewById(R.id.buttonDoneFavoriteTaskRecyclerView)

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