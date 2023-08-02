package com.example.task_app_droid_youtube.viewmodel

import android.net.http.HttpException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.task_app_droid_youtube.TestCoroutineRule
import com.example.task_app_droid_youtube.core.ViewState
import com.example.task_app_droid_youtube.model.Priority
import com.example.task_app_droid_youtube.model.TaskCreateRequest
import com.example.task_app_droid_youtube.model.TaskFetchResponse
import com.example.task_app_droid_youtube.model.TaskUpdateRequest
import com.example.task_app_droid_youtube.repository.TaskRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.confirmVerified
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import retrofit2.Response


@ExperimentalCoroutinesApi
internal class TaskViewModelImplTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @RelaxedMockK
    private lateinit var mockHttpException: HttpException

    @RelaxedMockK
    private lateinit var responseObserver: Observer<ViewState<TaskFetchResponse>>

    @RelaxedMockK
    private lateinit var responseObservers: Observer<ViewState<List<TaskFetchResponse>>>

    @RelaxedMockK
    private lateinit var responseObserverDelete: Observer<Boolean>

    @RelaxedMockK
    private lateinit var mockRepository: TaskRepository

    @InjectMockKs
    private lateinit var objectUnderTest: TaskViewModelImpl

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

    private val fetchResponse = TaskFetchResponse(
        id = 1,
        description = "write Tests",
        isReminderSet = false,
        isTaskOpen = true,
        createdOn = null,
        priority = Priority.LOW
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        objectUnderTest.task.removeObserver(responseObserver)
        objectUnderTest.tasks.removeObserver(responseObservers)
        objectUnderTest.isDeleteSuccessful.removeObserver(responseObserverDelete)
    }


    @Test
    fun `when calling for task by id then expect loading state`() {
        // given
        coEvery { mockRepository.getTaskById(any()) } returns ViewState.Loading
        objectUnderTest.task.observeForever(responseObserver)

        // when
        objectUnderTest.fetchTaskById("22")

        // then
        verify { responseObserver.onChanged(ViewState.Loading) }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when calling for task by id then expect success state`() {
        // given
        coEvery { mockRepository.getTaskById(any()) } returns ViewState.Success(fetchResponse)
        objectUnderTest.task.observeForever(responseObserver)

        // when
        objectUnderTest.fetchTaskById("1044")

        verify {
            responseObserver.onChanged(ViewState.Loading)
            responseObserver.onChanged(ViewState.Success(fetchResponse))
        }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when calling for task by id then expect error state`() {
        // given
        coEvery { mockRepository.getTaskById(any()) } returns ViewState.Error(mockHttpException)
        objectUnderTest.task.observeForever(responseObserver)

        // when
        objectUnderTest.fetchTaskById("999")

        verify {
            responseObserver.onChanged(ViewState.Loading)
            responseObserver.onChanged(ViewState.Error(mockHttpException))
        }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when creating a task then expect loading state`() {
        // given
        coEvery { mockRepository.createTask(any()) } returns ViewState.Loading
        objectUnderTest.task.observeForever(responseObserver)

        // when
        objectUnderTest.createTask(createRequest)

        // then
        verify { responseObserver.onChanged(ViewState.Loading) }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when creating a task then expect success state`() {
        // given
        coEvery { mockRepository.createTask(any()) } returns ViewState.Success(fetchResponse)
        objectUnderTest.task.observeForever(responseObserver)

        // when
        objectUnderTest.createTask(createRequest)

        // then
        verifyOrder {
            responseObserver.onChanged(ViewState.Loading)
            responseObserver.onChanged(ViewState.Success(fetchResponse))
        }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when creating a task then expect error state`() {
        // given
        coEvery { mockRepository.createTask(any()) } returns ViewState.Error(mockHttpException)
        objectUnderTest.task.observeForever(responseObserver)

        // when
        objectUnderTest.createTask(createRequest)

        // then
        verifyOrder {
            responseObserver.onChanged(ViewState.Loading)
            responseObserver.onChanged(ViewState.Error(mockHttpException))
        }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when calling for task list then expect loading state`() {
        // given
        coEvery { mockRepository.getTasks(null) } returns ViewState.Loading
        objectUnderTest.tasks.observeForever(responseObservers)

        // when
        objectUnderTest.fetchTasks(null)

        // then
        verify { responseObservers.onChanged(ViewState.Loading) }
        confirmVerified(responseObservers)
    }

    @Test
    fun `when fetching task list then expect a successful response`() {
        // given
        val expectedResponse = ArrayList<TaskFetchResponse>()
        coEvery { mockRepository.getTasks(null) } returns ViewState.Success(expectedResponse)
        objectUnderTest.tasks.observeForever(responseObservers)

        // when
        objectUnderTest.fetchTasks(null)

        // then
        verifyOrder {
            responseObservers.onChanged(ViewState.Loading)
            responseObservers.onChanged(ViewState.Success(expectedResponse))
        }
        confirmVerified(responseObservers)
    }

    @Test
    fun `when fetching task list then expect an http exception`() {
        // given
        coEvery { mockRepository.getTasks(null) } returns ViewState.Error(mockHttpException)
        objectUnderTest.tasks.observeForever(responseObservers)

        // when
        objectUnderTest.fetchTasks(null)

        // then
        verifyOrder {
            responseObservers.onChanged(ViewState.Loading)
            responseObservers.onChanged(ViewState.Error(mockHttpException))
        }
        confirmVerified(responseObservers)
    }

    @Test
    fun `when calling update task then expect loading state`() {
        // given
        coEvery { mockRepository.updateTask(any(), any()) } returns ViewState.Loading
        objectUnderTest.task.observeForever(responseObserver)

        // when
        objectUnderTest.updateTask("3", updateRequest)

        // then
        verify { responseObserver.onChanged(ViewState.Loading) }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when calling update task then expect successful response`() {
        // given
        coEvery { mockRepository.updateTask(any(), any()) } returns ViewState.Success(fetchResponse)
        objectUnderTest.task.observeForever(responseObserver)

        // when
        objectUnderTest.updateTask("1", updateRequest)

        // then
        verifyOrder {
            responseObserver.onChanged(ViewState.Loading)
            responseObserver.onChanged(ViewState.Success(fetchResponse))
        }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when calling update task then expect an http exception`() {
        // given
        coEvery { mockRepository.updateTask(any(), any()) } returns ViewState.Error(
            mockHttpException
        )
        objectUnderTest.task.observeForever(responseObserver)

        // when
        objectUnderTest.updateTask("1", updateRequest)

        // then
        verifyOrder {
            responseObserver.onChanged(ViewState.Loading)
            responseObserver.onChanged(ViewState.Error(mockHttpException))
        }
        confirmVerified(responseObserver)
    }

    @Test
    fun `when calling delete task then expect successful response`() {
        // given
        val isSuccess = Response.success(true)
        coEvery { mockRepository.canDeleteTask(any()) } returns ViewState.Success(isSuccess)
        objectUnderTest.isDeleteSuccessful.observeForever(responseObserverDelete)

        // when
        objectUnderTest.deleteTask("4")


        // then
        verifyOrder { responseObserverDelete.onChanged(true) }
        confirmVerified(responseObserverDelete)
    }

    @Test
    fun `when calling delete task then expect http exception`() {
        // given
        coEvery { mockRepository.canDeleteTask(any()) } returns ViewState.Error(mockHttpException)
        objectUnderTest.isDeleteSuccessful.observeForever(responseObserverDelete)

        // when
        objectUnderTest.deleteTask("9")

        // then
        verifyOrder {
            responseObserverDelete.onChanged(false)
        }
        confirmVerified(responseObserverDelete)
    }
}