package com.example.task_app_droid_youtube.core

sealed class ViewState<out T : Any> {

    object Loading : ViewState<Nothing>()
    data class Success<out T : Any>(val data: T) : ViewState<T>()
    data class Error(val exception: Exception) : ViewState<Nothing>()

    val extractData: T?
        get() = when (this) {
            is Success -> data
            is Error -> null
            Loading -> null
        }
}