package com.app.edonymyeon.presentation.ui.main.alarm.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.edonymyeon.R
import app.edonymyeon.databinding.ItemAlarmBinding
import com.app.edonymyeon.presentation.uimodel.NotificationUiModel

class AlarmViewHolder(
    parent: ViewGroup,
    private val onClick: (Int) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false),
) {
    private val binding = ItemAlarmBinding.bind(itemView)

    init {
        binding.root.setOnClickListener {
            onClick(bindingAdapterPosition)
        }
    }

    fun bind(notification: NotificationUiModel) {
        binding.notification = notification
        binding.executePendingBindings()
    }
}
