package com.app.edonymyeon.presentation.ui.main.alarm.adapter

import androidx.recyclerview.widget.DiffUtil
import com.app.edonymyeon.presentation.uimodel.NotificationUiModel

object AlarmDiffUtilCallback : DiffUtil.ItemCallback<NotificationUiModel>() {
    override fun areItemsTheSame(
        oldItem: NotificationUiModel,
        newItem: NotificationUiModel,
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: NotificationUiModel,
        newItem: NotificationUiModel,
    ): Boolean {
        return oldItem == newItem
    }
}
