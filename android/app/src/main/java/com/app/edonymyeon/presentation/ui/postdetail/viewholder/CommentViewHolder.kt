package com.app.edonymyeon.presentation.ui.postdetail.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.edonymyeon.R
import app.edonymyeon.databinding.ItemCommentBinding
import com.app.edonymyeon.presentation.ui.postdetail.listener.CommentClickListener
import com.app.edonymyeon.presentation.uimodel.CommentUiModel

class CommentViewHolder(
    parent: ViewGroup,
    commentClickListener: CommentClickListener,
    isLogin: Boolean,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.item_comment,
        parent,
        false,
    ),
) {
    private val binding = ItemCommentBinding.bind(itemView)

    init {
        binding.listener = commentClickListener
        binding.isLogin = isLogin
    }

    fun bind(commentUiModel: CommentUiModel) {
        binding.comment = commentUiModel
    }
}
