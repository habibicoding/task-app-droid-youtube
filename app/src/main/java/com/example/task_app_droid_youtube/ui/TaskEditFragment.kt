package com.example.task_app_droid_youtube.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.task_app_droid_youtube.R
import com.example.task_app_droid_youtube.core.ViewBindingFragment
import com.example.task_app_droid_youtube.databinding.FragmentEditTaskBinding
import com.example.task_app_droid_youtube.model.Priority
import com.example.task_app_droid_youtube.model.TaskUpdateRequest
import com.example.task_app_droid_youtube.viewmodel.TaskViewModelImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TaskEditFragment : ViewBindingFragment<FragmentEditTaskBinding>() {

    private val args by navArgs<TaskEditFragmentArgs>()
    private val viewModel by viewModels<TaskViewModelImpl>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditTaskBinding = FragmentEditTaskBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTwoWayDataBinding()

        setupTaskUpdate()
    }

    private fun setupTaskUpdate() {
        binding.editTaskBtn.setOnClickListener {
            val selectedPriority: Priority =
                when (binding.editTaskPriorityRadioGroup.checkedRadioButtonId) {
                    R.id.priority_low -> Priority.LOW
                    R.id.priority_medium -> Priority.MEDIUM
                    R.id.priority_high -> Priority.HIGH
                    else -> Priority.LOW
                }
            val updateRequest = TaskUpdateRequest(
                description = binding.editTaskDescriptionInput.text.toString(),
                isReminderSet = binding.editTaskSetReminderCheckBox.isChecked,
                isTaskOpen = binding.editTaskIsTaskOpenBox.isChecked,
                priority = selectedPriority
            )
            lifecycleScope.launch(Dispatchers.Main) {
                async { viewModel.updateTask(args.taskItem.id.toString(), updateRequest) }.await()
                findNavController().popBackStack()
            }
        }
    }

    private fun setupTwoWayDataBinding() {
        binding.task = args.taskItem
        args.taskItem.isTaskOpen?.let { isOpen -> binding.editTaskIsTaskOpenBox.isChecked = isOpen }
        args.taskItem.isReminderSet?.let { isReminderSet ->
            binding.editTaskSetReminderCheckBox.isChecked = isReminderSet
        }
        when (args.taskItem.priority) {
            Priority.LOW -> binding.priorityLow.isChecked = true
            Priority.MEDIUM -> binding.priorityMedium.isChecked = true
            else -> binding.priorityHigh.isChecked = true
        }
    }


}