package com.example.task_app_droid_youtube.repository

import com.example.task_app_droid_youtube.BaseRepositoryTest
import com.example.task_app_droid_youtube.model.Priority
import com.example.task_app_droid_youtube.model.TaskCreateRequest
import com.example.task_app_droid_youtube.model.TaskUpdateRequest
import com.example.task_app_droid_youtube.networking.TaskApi
import com.google.gson.GsonBuilder
import io.mockk.mockk
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

internal class TaskRepositoryImplTest : BaseRepositoryTest() {

    companion object {
        private const val TASKS_RESPONSE = "get_tasks_response.json"
        private const val TASK_BY_ID_RESPONSE = "get_task_by_id_response.json"
        private const val TASK_POST_REQUEST = "post_request_task.json"
        private const val TASK_PATCH_REQUEST = "patch_request_task.json"
        private const val INTERNAL_SERVER_ERROR: Int = 500
    }

    private val gson = GsonBuilder().create()

    private val exception = Exception("error triggered")

    private val createRequest = TaskCreateRequest(
        description = "Buy Hummus",
        isReminderSet = true,
        isTaskOpen = true,
        priority = Priority.MEDIUM
    )

    private val updateRequest = TaskUpdateRequest(
        description = "Try new Shawarma place",
        isReminderSet = false,
        isTaskOpen = null,
        priority = Priority.LOW
    )

    private lateinit var taskApi: TaskApi
    private lateinit var objectUnderTest: TaskRepository

    private val mockTaskApi = mockk<TaskApi>()
    private val mockRepository = TaskRepositoryImpl(mockTaskApi)

    override fun setUp() {
        super.setUp()

        taskApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(TaskApi::class.java)

        objectUnderTest = TaskRepositoryImpl(taskApi)
    }


}