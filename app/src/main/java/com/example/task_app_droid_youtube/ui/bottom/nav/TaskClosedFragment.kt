package com.example.task_app_droid_youtube.ui.bottom.nav

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
class TaskClosedFragment : TaskFragment() {

    override fun onResume() {
        super.onResume()
        callViewModel()
    }

    override fun initializeController() {
        val color = ContextCompat.getColor(requireContext(), R.color.green_dark)
        controller = TaskEpoxyController(color)
        binding.fabLayout.visibility = View.GONE
    }

    override fun callViewModel() {
        viewModel.fetchTasks(TaskStatus.CLOSED.toString())
    }

    override fun navigateToEditTask(task: TaskFetchResponse) {
        TODO("Not yet implemented")
    }

    override fun navigateToDetailTask(task: TaskFetchResponse) {
        TODO("Not yet implemented")
    }
}
