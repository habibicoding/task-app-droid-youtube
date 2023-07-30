package com.example.task_app_droid_youtube.repository

import com.example.task_app_droid_youtube.core.ViewState
import com.example.task_app_droid_youtube.model.TaskCreateRequest
import com.example.task_app_droid_youtube.model.TaskFetchResponse
import com.example.task_app_droid_youtube.model.TaskUpdateRequest
import retrofit2.Response

interface TaskRepository {

    suspend fun getTasks(status: String?): ViewState<List<TaskFetchResponse>>

    suspend fun getTaskById(id: String): ViewState<TaskFetchResponse>

    suspend fun createTask(createRequest: TaskCreateRequest): ViewState<TaskFetchResponse>

    suspend fun canDeleteTask(id: String): ViewState<Response<Boolean>>

    suspend fun updateTask(
        id: String,
        updateRequest: TaskUpdateRequest
    ): ViewState<TaskFetchResponse>
}