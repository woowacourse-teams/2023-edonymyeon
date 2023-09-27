package com.app.edonymyeon.presentation.ui.postdetail.adapter

import androidx.recyclerview.widget.DiffUtil
import com.app.edonymyeon.presentation.uimodel.CommentUiModel

object CommentDiffUtilCallback : DiffUtil.ItemCallback<CommentUiModel>() {
    override fun areItemsTheSame(oldItem: CommentUiModel, newItem: CommentUiModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CommentUiModel, newItem: CommentUiModel): Boolean {
        return oldItem == newItem
    }
}
