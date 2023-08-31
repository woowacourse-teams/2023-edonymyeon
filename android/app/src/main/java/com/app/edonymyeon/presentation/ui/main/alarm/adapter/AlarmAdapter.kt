package com.app.edonymyeon.presentation.ui.main.alarm.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.app.edonymyeon.presentation.ui.main.alarm.viewholder.AlarmViewHolder
import com.app.edonymyeon.presentation.uimodel.NotificationUiModel

class AlarmAdapter(private val onClick: (NotificationUiModel) -> Unit) :
    ListAdapter<NotificationUiModel, AlarmViewHolder>(AlarmDiffUtilCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        return AlarmViewHolder(parent, onClick = {
            onClick(currentList[it])
        })
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    fun setAllPosts(notifications: List<NotificationUiModel>) {
        submitList(notifications)
    }
}
