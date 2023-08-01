package com.example.task_app_droid_youtube.repository

import com.example.task_app_droid_youtube.BaseRepositoryTest
import com.example.task_app_droid_youtube.FileReader
import com.example.task_app_droid_youtube.core.ViewState
import com.example.task_app_droid_youtube.model.Priority
import com.example.task_app_droid_youtube.model.TaskCreateRequest
import com.example.task_app_droid_youtube.model.TaskFetchResponse
import com.example.task_app_droid_youtube.model.TaskUpdateRequest
import com.example.task_app_droid_youtube.networking.TaskApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class TaskRepositoryImplTest : BaseRepositoryTest() {

    companion object {
        private const val TASKS_RESPONSE = "get_tasks_response.json"
        private const val TASK_BY_ID_RESPONSE = "get_task_by_id_response.json"
        private const val TASK_POST_REQUEST = "post_request_task.json"
        private const val TASK_PATCH_REQUEST = "patch_request_task.json"
        private const val INTERNAL_SERVER_ERROR: Int = 500
        private const val SOMETHING_WRONG = "Something went wrong"
    }

    private val gsonBuilder = GsonBuilder().create()

    private val customException = Exception("error triggered")

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

    @Before
    override fun setUp() {
        super.setUp()

        taskApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder))
            .build()
            .create(TaskApi::class.java)

        objectUnderTest = TaskRepositoryImpl(taskApi)
    }


    @Test
    fun `when fetch request for task by id then check for success response`() {
        // given
        val json = getJsonString<TaskFetchResponse>(TASK_BY_ID_RESPONSE)
        val gson = Gson()
        val expectedTask: TaskFetchResponse = gson.fromJson(json, TaskFetchResponse::class.java)
        mockWebServer.enqueue(MockResponse().setBody(FileReader(TASK_BY_ID_RESPONSE).content))

        runBlocking {
            // when
            val actualTask: TaskFetchResponse? = objectUnderTest.getTaskById("1").extractData
            // then
            assertEquals(expectedTask, actualTask)
        }
    }

    @Test
    fun `when fetch request for task by id then check for http exception`() {
        // given
        mockWebServer.enqueue(MockResponse().setResponseCode(INTERNAL_SERVER_ERROR))

        runBlocking {
            // when
            val actualResult: ViewState<TaskFetchResponse> = objectUnderTest.getTaskById("12")

            // then
            assertTrue(actualResult is ViewState.Error)
            val actualMessage = (actualResult as ViewState.Error).exception.message
            assertEquals(SOMETHING_WRONG, actualMessage)
        }
    }

    @Test
    fun `when fetch request for task by id then check for unknown exception`() {
        // given
        coEvery { mockRepository.getTaskById(any()) } throws customException

        runBlocking {
            // when
            val actualResult: ViewState<TaskFetchResponse> = mockRepository.getTaskById("111")

            //then
            assertTrue(actualResult is ViewState.Error)
            val actualMessage = (actualResult as ViewState.Error).exception.message
            assertEquals(customException.message, actualMessage)
        }
    }

    @Test
    fun `when task post request then check for success response`() {
        // given
        val json = getJsonString<TaskFetchResponse>(TASK_POST_REQUEST)
        val gson = Gson()
        val expectedTask: TaskFetchResponse = gson.fromJson(json, TaskFetchResponse::class.java)
        mockWebServer.enqueue(MockResponse().setBody(FileReader(TASK_POST_REQUEST).content))

        runBlocking {
            // when
            val actualTask: TaskFetchResponse? =
                objectUnderTest.createTask(createRequest).extractData

            // then
            assertEquals(expectedTask, actualTask)
        }
    }


    @Test
    fun `when task post request then check for http exception`() {
        // given
        mockWebServer.enqueue(MockResponse().setResponseCode(INTERNAL_SERVER_ERROR))

        runBlocking {
            // when
            val actualResult: ViewState<TaskFetchResponse> =
                objectUnderTest.createTask(createRequest)

            // then
            assertTrue(actualResult is ViewState.Error)
            val actualMessage = (actualResult as ViewState.Error).exception.message
            assertEquals(SOMETHING_WRONG, actualMessage)
        }
    }

    @Test
    fun `when task post request then check for unknown exception`() {
        // given
        coEvery { mockRepository.createTask(any()) } throws customException

        runBlocking {
            // when
            val actualResult: ViewState<TaskFetchResponse> =
                mockRepository.createTask(createRequest)

            // then
            assertTrue(actualResult is ViewState.Error)
            val actualMessage = (actualResult as ViewState.Error).exception.message
            assertEquals(customException.message, actualMessage)
        }
    }
}