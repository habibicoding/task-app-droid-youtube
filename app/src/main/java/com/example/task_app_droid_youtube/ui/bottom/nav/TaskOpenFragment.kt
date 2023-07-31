package com.example.task_app_droid_youtube.ui.bottom.nav

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
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
        val action = TaskOpenFragmentDirections.actionOpenTaskToTaskEdit(task)
        findNavController().navigate(action)
    }

    override fun navigateToDetailTask(task: TaskFetchResponse) {
        val action = TaskOpenFragmentDirections.actionTaskOpenToTaskDetail(task.id)
        findNavController().navigate(action)
    }

    private fun setUpCreateTaskButton() {
        binding.fabBtn.setOnClickListener {
            val action = TaskOpenFragmentDirections.actionOpenTaskToTaskCreate()
            findNavController().navigate(action)
        }
    }
}
