package com.app.edonymyeon.presentation.ui.main.home.adapter

import androidx.recyclerview.widget.DiffUtil
import com.app.edonymyeon.presentation.uimodel.AllPostItemUiModel

object AllPostDiffUtilCallback : DiffUtil.ItemCallback<AllPostItemUiModel>() {
    override fun areItemsTheSame(
        oldItem: AllPostItemUiModel,
        newItem: AllPostItemUiModel,
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: AllPostItemUiModel,
        newItem: AllPostItemUiModel,
    ): Boolean {
        return oldItem == newItem
    }
}
