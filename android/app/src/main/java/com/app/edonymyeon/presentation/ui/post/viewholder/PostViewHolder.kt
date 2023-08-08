package com.app.edonymyeon.presentation.ui.post.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.edonymyeon.R
import com.app.edonymyeon.databinding.ItemPostBinding
import com.app.edonymyeon.presentation.uimodel.PostItemUiModel
import com.app.edonymyeon.presentation.uimodel.ReactionCountUiModel

class PostViewHolder(parent: ViewGroup, onClick: (Int) -> Unit) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false),
    ) {
    val binding = ItemPostBinding.bind(itemView)

    init {
        binding.root.setOnClickListener {
            onClick(bindingAdapterPosition)
        }
    }

    fun bind(post: PostItemUiModel) {
        binding.post = post
        binding.prvAllPostReaction.reactionCount = ReactionCountUiModel(
            post.reactionCount.viewCount,
            post.reactionCount.commentCount,
            post.reactionCount.scrapCount,
        )
    }
}
