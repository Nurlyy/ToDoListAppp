package com.example.todolistapp.Tasks

data class Task(
    val taskId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val dateTimeInMillis: Long = 0,
    val notification: Boolean = false,
    val notificationID: Int? = 0,
    var file: String? = null,
    var isDone:Boolean = false,
    var isDeleted:Boolean = false
)