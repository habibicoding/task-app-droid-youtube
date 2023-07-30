package com.example.task_app_droid_youtube.ui.bottom.nav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.task_app_droid_youtube.R
import com.example.task_app_droid_youtube.core.ViewBindingFragment
import com.example.task_app_droid_youtube.databinding.FragmentTaskBinding
import com.example.task_app_droid_youtube.model.TaskFetchResponse
import com.example.task_app_droid_youtube.ui.view.SwipeGestures
import com.example.task_app_droid_youtube.ui.view.epoxy.TaskEpoxyController
import com.example.task_app_droid_youtube.util.displayAlertDialog
import com.example.task_app_droid_youtube.viewmodel.TaskViewModelImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
abstract class TaskFragment : ViewBindingFragment<FragmentTaskBinding>(),
    SwipeRefreshLayout.OnRefreshListener {

    protected val viewModel by viewModels<TaskViewModelImpl>()
    protected lateinit var controller: TaskEpoxyController
    private val tasks = mutableListOf<TaskFetchResponse>()

    protected abstract fun initializeController()
    protected abstract fun callViewModel()
    protected abstract fun navigateToEditTask(task: TaskFetchResponse)
    protected abstract fun navigateToDetailTask(task: TaskFetchResponse)


    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTaskBinding = FragmentTaskBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSwipeGestures()
        initializeController()
        setupRecyclerView()
        observeTaskLiveData()
        observeDeleteTaskLiveData()
        clickOnRetry()
        setupSwipeRefresh()
    }

    override fun onRefresh() {
        callViewModel()
    }

    private fun setupSwipeGestures() {
        val swipeGestures = object : SwipeGestures(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val myTask = controller.getTaskById(viewHolder.absoluteAdapterPosition)
                myTask.let { task ->
                    when (direction) {
                        ItemTouchHelper.LEFT -> {
                            displayAlertDialog(
                                task.id.toString(),
                                requireContext(),
                                getString(R.string.delete_task_headline),
                                viewModel
                            )
                            controller.notifyModelChanged(viewHolder.absoluteAdapterPosition)
                        }

                        ItemTouchHelper.RIGHT -> navigateToEditTask(task)
                    }
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipeGestures)
        touchHelper.attachToRecyclerView(binding.recyclerView)
    }
}