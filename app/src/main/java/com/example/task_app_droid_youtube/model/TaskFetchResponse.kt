package com.example.task_app_droid_youtube.model


data class TaskFetchResponse(
    val id: Long,
    val description: String,
    val isReminderSet: Boolean?,
    val isTaskOpen: Boolean?,
    val createdOn: String,
    val priority: Priority?
)
