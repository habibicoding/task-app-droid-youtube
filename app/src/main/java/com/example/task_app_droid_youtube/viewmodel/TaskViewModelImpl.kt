package com.example.task_app_droid_youtube.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_app_droid_youtube.core.ViewState
import com.example.task_app_droid_youtube.model.TaskCreateRequest
import com.example.task_app_droid_youtube.model.TaskFetchResponse
import com.example.task_app_droid_youtube.model.TaskUpdateRequest
import com.example.task_app_droid_youtube.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class TaskViewModelImpl @Inject constructor(
    private val repository: TaskRepository
) : ViewModel(), TaskViewModel {

    private val _tasks = MutableLiveData<ViewState<List<TaskFetchResponse>>>()
    val tasks: LiveData<ViewState<List<TaskFetchResponse>>>
        get() = _tasks

    private val _task = MutableLiveData<ViewState<TaskFetchResponse>>()
    val task: LiveData<ViewState<TaskFetchResponse>>
        get() = _task

    private val _isDeleteSuccessful = MutableLiveData<Boolean>()
    val isDeleteSuccessful: LiveData<Boolean>
        get() = _isDeleteSuccessful

    override fun fetchTasks(status: String?) {
        _tasks.postValue(ViewState.Loading)
        viewModelScope.launch {
            handleResponse(_tasks, repository.getTasks(status))
        }
    }

    override fun fetchTaskById(id: String) {
        _task.postValue(ViewState.Loading)
        viewModelScope.launch {
            handleResponse(_task, repository.getTaskById(id))
        }
    }

    override fun createTask(createRequest: TaskCreateRequest) {
        _task.postValue(ViewState.Loading)
        viewModelScope.launch {
            handleResponse(_task, repository.createTask(createRequest))
        }
    }

    override fun deleteTask(id: String) {
        viewModelScope.launch {
            when (repository.canDeleteTask(id)) {
                is ViewState.Success -> _isDeleteSuccessful.postValue(true)
                is ViewState.Error -> _isDeleteSuccessful.postValue(false)
                else -> Timber.d("ViewModel delete task in progress")
            }
        }
    }

    override fun updateTask(id: String, updateRequest: TaskUpdateRequest) {
        _task.postValue(ViewState.Loading)
        viewModelScope.launch {
            handleResponse(_task, repository.updateTask(id, updateRequest))
        }
    }

    private fun <T : Any> handleResponse(
        liveData: MutableLiveData<ViewState<T>>,
        response: ViewState<T>
    ) {
        liveData.postValue(response)
        when (response) {
            is ViewState.Success -> Timber.d("success case: $response")
            is ViewState.Error -> Timber.d("error case: $response")
            else -> Timber.d("else case: $response")
        }
    }
}