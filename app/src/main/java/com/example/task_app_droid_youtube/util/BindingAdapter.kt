package com.example.task_app_droid_youtube.util

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.task_app_droid_youtube.R

@BindingAdapter("formattedDate")
fun bindFormattedDate(view: TextView, dateString: String?) {
    if (dateString != null) {
        val formattedDate = DateUtil.formatISO8601Date(dateString)
        view.text = view.context.getString(R.string.created_on_text, formattedDate)
    }
}