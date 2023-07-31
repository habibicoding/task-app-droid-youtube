package com.example.task_app_droid_youtube.ui.bottom.nav

import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.task_app_droid_youtube.R
import com.example.task_app_droid_youtube.model.TaskFetchResponse
import com.example.task_app_droid_youtube.ui.view.epoxy.TaskEpoxyController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TaskAllFragment : TaskFragment() {

    override fun onResume() {
        super.onResume()
        callViewModel()
    }

    override fun initializeController() {
        val color = ContextCompat.getColor(requireContext(), R.color.black)
        controller = TaskEpoxyController(color)
        binding.fabLayout.visibility = View.GONE
    }

    override fun callViewModel() {
        viewModel.fetchTasks(null)
    }

    override fun navigateToEditTask(task: TaskFetchResponse) {
        val action = TaskAllFragmentDirections.actionTaskAllToTaskEdit(task)
        findNavController().navigate(action)
    }

    override fun navigateToDetailTask(task: TaskFetchResponse) {
        val action = TaskAllFragmentDirections.actionTaskAllToTaskDetail(task.id)
        findNavController().navigate(action)
    }
}

