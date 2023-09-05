package com.app.edonymyeon.presentation.ui.postdetail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.app.edonymyeon.presentation.ui.postdetail.listener.CommentClickListener
import com.app.edonymyeon.presentation.ui.postdetail.viewholder.CommentViewHolder
import com.app.edonymyeon.presentation.uimodel.CommentUiModel

class CommentAdapter(
    private val commentClickListener: CommentClickListener,
) : ListAdapter<CommentUiModel, CommentViewHolder>(CommentDiffUtilCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(parent, commentClickListener)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        return holder.bind(currentList[position])
    }

    fun setComments(comments: List<CommentUiModel>) {
        submitList(comments)
    }
}
