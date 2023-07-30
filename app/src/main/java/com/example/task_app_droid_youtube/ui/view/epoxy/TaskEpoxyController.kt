package com.example.task_app_droid_youtube.ui.view.epoxy

import com.airbnb.epoxy.EpoxyController
import com.example.task_app_droid_youtube.itemTask
import com.example.task_app_droid_youtube.model.TaskFetchResponse

class TaskEpoxyController(private val cardBackgroundColor: Int) : EpoxyController() {

    private val tasks = mutableListOf<TaskFetchResponse>()

    override fun buildModels() {
        this.tasks.forEachIndexed { _, model ->
            itemTask {
                id(model.id)
                taskResponse(model)
                bgColor(this@TaskEpoxyController.cardBackgroundColor)
            }
        }
    }

    fun setTasks(items: List<TaskFetchResponse>) {
        this.tasks.clear()
        this.tasks.addAll(items)
        requestModelBuild()
    }

    fun getNumberOfTasks(): Int = this.tasks.size

    fun getTaskById(index: Int): TaskFetchResponse = this.tasks[index]
}