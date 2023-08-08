package com.app.edonymyeon.presentation.ui.mypost.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.edonymyeon.R
import com.app.edonymyeon.databinding.ItemMyPostBinding
import com.app.edonymyeon.presentation.ui.mypost.listener.MyPostClickListener
import com.app.edonymyeon.presentation.uimodel.MyPostUiModel

class MyPostViewHolder(
    parent: ViewGroup,
    clickListener: MyPostClickListener,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.item_my_post,
        parent,
        false,
    ),
) {
    private val binding = ItemMyPostBinding.bind(itemView)

    init {
        binding.listener = clickListener
    }

    fun bind(myPost: MyPostUiModel) {
        binding.post = myPost
    }
}
