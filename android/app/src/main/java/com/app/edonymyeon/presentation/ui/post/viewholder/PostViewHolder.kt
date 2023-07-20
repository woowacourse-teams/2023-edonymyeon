package com.app.edonymyeon.presentation.ui.post.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.edonymyeon.R
import app.edonymyeon.databinding.ItemPostBinding
import com.app.edonymyeon.presentation.uimodel.PostItemUiModel

class PostViewHolder(parent: ViewGroup, onClick: (Int) -> Unit) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
    ) {
    val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    init {
        binding.root.setOnClickListener {
            onClick(bindingAdapterPosition)
        }
    }

    fun bind(post: PostItemUiModel) {
        binding.post = post

    }
}