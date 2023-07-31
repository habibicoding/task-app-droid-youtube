package com.example.task_app_droid_youtube.ui.bottom.nav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.task_app_droid_youtube.R
import com.example.task_app_droid_youtube.core.ViewBindingFragment
import com.example.task_app_droid_youtube.core.ViewState
import com.example.task_app_droid_youtube.databinding.FragmentTaskBinding
import com.example.task_app_droid_youtube.model.TaskFetchResponse
import com.example.task_app_droid_youtube.ui.view.SwipeGestures
import com.example.task_app_droid_youtube.ui.view.epoxy.TaskEpoxyController
import com.example.task_app_droid_youtube.util.displayAlertDialog
import com.example.task_app_droid_youtube.util.showToastMessage
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

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.let {
            it.setOnRefreshListener(this)
            it.setColorSchemeResources(R.color.purple_200)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            this.setHasFixedSize(true)
            this.itemAnimator = DefaultItemAnimator()
            this.adapter = controller.adapter
        }
    }

    private fun observeTaskLiveData() {
        viewModel.tasks.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Loading -> {
                    binding.recyclerView.visibility = View.GONE
                    binding.shimmerFrame.startShimmerAnimation()
                }

                is ViewState.Success -> {
                    this.tasks.clear()
                    if (response.data.isEmpty()) {
                        showEmptyScreen()
                    } else {
                        showArticlesOnScreen()
                    }
                    val fetchedTasks = response.data.map { task ->
                        task.onClick = View.OnClickListener { navigateToTaskDetail(task) }
                        task
                    }
                    this.tasks.addAll(fetchedTasks)
                    this.controller.setTasks(this.tasks)
                    if (controller.getNumberOfTasks() == 0) {
                        showToastMessage(requireContext(), getString(R.string.no_tasks_found))
                    }
                }

                is ViewState.Error -> {
                    showEmptyScreen()
                }
            }
        }
    }

    private fun showEmptyScreen() {
        with(binding) {
            shimmerFrame.stopShimmerAnimation()
            shimmerFrame.visibility = View.GONE
            recyclerView.visibility = View.GONE
            emptyText.visibility = View.VISIBLE
            retryFetchButton.visibility = View.VISIBLE
        }
        controller.setTasks(emptyList())
    }

    private fun showArticlesOnScreen() {
        with(binding) {
            shimmerFrame.stopShimmerAnimation()
            shimmerFrame.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            emptyText.visibility = View.GONE
            retryFetchButton.visibility = View.GONE
            swipeRefresh.isRefreshing = false
        }
    }
}