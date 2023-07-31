package com.example.task_app_droid_youtube.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.task_app_droid_youtube.R
import com.example.task_app_droid_youtube.core.ViewBindingFragment
import com.example.task_app_droid_youtube.core.ViewState
import com.example.task_app_droid_youtube.databinding.FragmentDetailTaskBinding
import com.example.task_app_droid_youtube.model.TaskFetchResponse
import com.example.task_app_droid_youtube.util.displayAlertDialog
import com.example.task_app_droid_youtube.util.showToastMessage
import com.example.task_app_droid_youtube.viewmodel.TaskViewModelImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TaskDetailFragment : ViewBindingFragment<FragmentDetailTaskBinding>() {

    private val args by navArgs<TaskDetailFragmentArgs>()
    private val viewModel by viewModels<TaskViewModelImpl>()
    private var taskResponse: TaskFetchResponse? = null

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailTaskBinding = FragmentDetailTaskBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeTaskLiveData()

        observeDeleteTaskLiveData()

        setupDeleteTask()

        setupEdiTask()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchTaskById(args.taskId.toString())
    }

    private fun observeTaskLiveData() {
        viewModel.task.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Loading -> showLoadingState()

                is ViewState.Success -> handleSuccessState(response.data)

                is ViewState.Error -> handleErrorState(response.exception.message.toString())
            }
        }
    }

    private fun showLoadingState() {
        binding.shimmerFrame.startShimmerAnimation()
    }

    private fun handleSuccessState(response: TaskFetchResponse) {
        taskResponse = response.apply { onClick = null }
        binding.task = taskResponse
        binding.shimmerFrame.stopShimmerAnimation()
        binding.taskDetailErrorText.visibility = View.GONE
    }

    private fun handleErrorState(message: String) {
        with(binding) {
            shimmerFrame.apply {
                stopShimmerAnimation()
                visibility = View.GONE
            }
            taskDetailErrorText.apply {
                text = message
                visibility = View.VISIBLE
            }
        }
    }

    private fun setupDeleteTask() {
        binding.taskDetailDeleteTaskBtn.setOnClickListener {
            taskResponse?.let {
                displayAlertDialog(
                    it.id.toString(),
                    requireContext(),
                    getString(R.string.delete_task_headline),
                    viewModel
                )
            }
        }
    }

    private fun setupEdiTask() {
        binding.taskDetailEditTaskBtn.setOnClickListener {
            taskResponse?.let {
                val action = TaskDetailFragmentDirections.actionTaskDetailToTaskEdit(it)
                findNavController().navigate(action)
            }
        }
    }

    private fun observeDeleteTaskLiveData() {
        viewModel.isDeleteSuccessful.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                showToastMessage(requireContext(), getString(R.string.task_request_success_message))
                findNavController().popBackStack()
            } else {
                showToastMessage(requireContext(), getString(R.string.task_request_failure_message))
                Timber.d("Delete status : $isSuccessful")
            }
        }
    }
}