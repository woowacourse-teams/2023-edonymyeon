package com.app.edonymyeon.presentation.ui.mypost.adapter

import androidx.recyclerview.widget.DiffUtil
import com.app.edonymyeon.presentation.uimodel.MyPostUiModel

object MyPostDiffUtilCallback : DiffUtil.ItemCallback<MyPostUiModel>() {
    override fun areItemsTheSame(oldItem: MyPostUiModel, newItem: MyPostUiModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MyPostUiModel, newItem: MyPostUiModel): Boolean {
        return oldItem == newItem
    }
}
