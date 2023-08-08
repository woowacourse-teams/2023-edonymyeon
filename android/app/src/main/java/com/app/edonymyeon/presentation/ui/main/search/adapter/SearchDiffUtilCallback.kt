package com.app.edonymyeon.presentation.ui.main.search.adapter

import androidx.recyclerview.widget.DiffUtil
import com.app.edonymyeon.presentation.uimodel.PostItemUiModel

object SearchDiffUtilCallback : DiffUtil.ItemCallback<PostItemUiModel>() {
    override fun areItemsTheSame(oldItem: PostItemUiModel, newItem: PostItemUiModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PostItemUiModel, newItem: PostItemUiModel): Boolean {
        return oldItem == newItem
    }
}
