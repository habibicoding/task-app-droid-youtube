package com.example.task_app_droid_youtube.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.task_app_droid_youtube.R
import com.example.task_app_droid_youtube.core.ViewBindingFragment
import com.example.task_app_droid_youtube.core.ViewState
import com.example.task_app_droid_youtube.databinding.FragmentCreateTaskBinding
import com.example.task_app_droid_youtube.model.Priority
import com.example.task_app_droid_youtube.model.TaskCreateRequest
import com.example.task_app_droid_youtube.util.showToastMessage
import com.example.task_app_droid_youtube.viewmodel.TaskViewModelImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TaskCreateFragment : ViewBindingFragment<FragmentCreateTaskBinding>() {

    private val viewModel by viewModels<TaskViewModelImpl>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateTaskBinding = FragmentCreateTaskBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeTaskCreateLiveData()

        binding.createTaskBtn.setOnClickListener {
            val task = createTaskItem()
            task?.let { viewModel.createTask(it) }
        }
    }

    private fun observeTaskCreateLiveData() {
        viewModel.task.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Loading -> Timber.d("Data is loading: ${response.extractData}")
                is ViewState.Success -> {
                    showToastMessage(
                        requireContext(),
                        getString(R.string.task_request_success_message)
                    )
                    findNavController().popBackStack()
                }

                is ViewState.Error -> {
                    val errorMessage = response.exception.message
                        ?: getString(R.string.task_request_failure_message)
                    showToastMessage(requireContext(), errorMessage)
                }
            }

        }
    }

    private fun createTaskItem(): TaskCreateRequest? {
        val description = binding.createTaskDescriptionInput.text.toString()
        val isReminderSet = binding.createTaskSetReminderCheckBox.isChecked

        if (description.isEmpty()) {
            showToastMessage(requireContext(), getString(R.string.enter_description))
            return null
        }

        val priority: Priority = when {
            binding.priorityLow.isChecked -> Priority.LOW
            binding.priorityMedium.isChecked -> Priority.MEDIUM
            else -> Priority.HIGH
        }

        return TaskCreateRequest(
            description = description,
            priority = priority,
            isReminderSet = isReminderSet,
            isTaskOpen = true
        )
    }
}