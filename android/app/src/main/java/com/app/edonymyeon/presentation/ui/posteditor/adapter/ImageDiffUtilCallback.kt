package com.app.edonymyeon.presentation.ui.posteditor.adapter

import androidx.recyclerview.widget.DiffUtil

object ImageDiffUtilCallback :
    DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}
