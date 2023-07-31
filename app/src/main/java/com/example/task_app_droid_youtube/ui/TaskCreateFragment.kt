package com.example.task_app_droid_youtube.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.task_app_droid_youtube.core.ViewBindingFragment
import com.example.task_app_droid_youtube.databinding.FragmentCreateTaskBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TaskCreateFragment : ViewBindingFragment<FragmentCreateTaskBinding>() {

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateTaskBinding = FragmentCreateTaskBinding.inflate(inflater)
}