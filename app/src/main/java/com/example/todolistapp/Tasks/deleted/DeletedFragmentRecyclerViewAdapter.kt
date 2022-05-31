package com.example.todolistapp.Tasks.deleted

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.R
import com.example.todolistapp.Tasks.Task
import java.text.SimpleDateFormat

class DeletedFragmentRecyclerViewAdapter(val sortedActiveTasks: ArrayList<Task>, private val listener: OnItemClickListener): RecyclerView.Adapter<DeletedFragmentRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.deleted_fragment_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = sortedActiveTasks[position]
        holder.textViewTitle.text = task.title
        holder.textViewDescription.text = task.description
    }

    override fun getItemCount(): Int {
        return sortedActiveTasks.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var textViewTitle: TextView
        var textViewDescription: TextView
        var buttonDelete: Button
        var buttonDone: Button

        init {
            textViewTitle = itemView.findViewById(R.id.textViewTitleDeletedTaskRecyclerView)
            textViewDescription = itemView.findViewById(R.id.textViewDescriptionDeletedTaskRecyclerView)
            buttonDelete = itemView.findViewById(R.id.buttonDeleteDeletedTaskRecyclerView)
            buttonDone = itemView.findViewById(R.id.buttonActivateDeleteTaskRecyclerView)

            textViewTitle.setOnClickListener(this)
            textViewDescription.setOnClickListener(this)
            buttonDelete.setOnClickListener(this)
            buttonDone.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            val position = adapterPosition

            if(position!=RecyclerView.NO_POSITION){
                if(view?.id == textViewTitle.id || view?.id == textViewDescription.id){
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

}