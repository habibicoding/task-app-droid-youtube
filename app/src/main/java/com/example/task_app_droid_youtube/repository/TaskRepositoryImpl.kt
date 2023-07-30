package com.example.task_app_droid_youtube.repository

import com.example.task_app_droid_youtube.core.BaseRepository
import com.example.task_app_droid_youtube.core.ViewState
import com.example.task_app_droid_youtube.model.TaskCreateRequest
import com.example.task_app_droid_youtube.model.TaskFetchResponse
import com.example.task_app_droid_youtube.model.TaskStatus
import com.example.task_app_droid_youtube.model.TaskUpdateRequest
import com.example.task_app_droid_youtube.networking.TaskApi
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val apiService: TaskApi
) : BaseRepository(), TaskRepository {

    companion object {
        private const val HTTP_EXCEPTION = "HTTP Exception"
        private const val SUCCESS_NO_CONTENT: Int = 204
    }

    override suspend fun getTasks(status: String?): ViewState<List<TaskFetchResponse>> =
        performSafeApiCall {
            val taskStatus = TaskStatus.values().find { it.name == status }?.toString()
            apiService.getTasks(taskStatus)
        }

    override suspend fun getTaskById(id: String): ViewState<TaskFetchResponse> =
        performSafeApiCall {
            apiService.getTaskById(id)
        }

    override suspend fun createTask(createRequest: TaskCreateRequest): ViewState<TaskFetchResponse> =
        performSafeApiCall {
            apiService.createTask(createRequest)
        }

    override suspend fun canDeleteTask(id: String): ViewState<Response<Boolean>> =
        performSafeApiCall {
            val response: Response<Boolean> = apiService.canDeleteTask(id)
            if (response.code() != SUCCESS_NO_CONTENT) throw HttpException(response)
            response
        }

    override suspend fun updateTask(
        id: String,
        updateRequest: TaskUpdateRequest
    ): ViewState<TaskFetchResponse> = performSafeApiCall {
        apiService.updateTask(id, updateRequest)
    }

    private suspend fun <T : Any> performSafeApiCall(callApi: suspend () -> T): ViewState<T> {
        return try {
            handleSuccess(callApi.invoke())
        } catch (error: HttpException) {
            Timber.e("$HTTP_EXCEPTION : ${error.message}")
            handleException(error.code())
        } catch (error: Exception) {
            Timber.e("Unknown Exception : ${error.message}")
            ViewState.Error(error)
        }
    }
}