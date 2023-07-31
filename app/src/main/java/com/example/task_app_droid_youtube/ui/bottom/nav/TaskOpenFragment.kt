package com.example.task_app_droid_youtube.ui.bottom.nav

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.example.task_app_droid_youtube.R
import com.example.task_app_droid_youtube.model.TaskFetchResponse
import com.example.task_app_droid_youtube.model.TaskStatus
import com.example.task_app_droid_youtube.ui.view.epoxy.TaskEpoxyController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TaskOpenFragment : TaskFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpCreateTaskButton()
    }

    override fun onResume() {
        super.onResume()
        callViewModel()
    }

    override fun initializeController() {
        val color = ContextCompat.getColor(requireContext(), R.color.darker_gray)
        controller = TaskEpoxyController(color)
    }

    override fun callViewModel() {
        viewModel.fetchTasks(TaskStatus.OPEN.toString())
    }

    override fun navigateToEditTask(task: TaskFetchResponse) {
        TODO("Not yet implemented")
    }

    override fun navigateToDetailTask(task: TaskFetchResponse) {
        TODO("Not yet implemented")
    }

    private fun setUpCreateTaskButton() {
        TODO("Not yet implemented")
    }
}
