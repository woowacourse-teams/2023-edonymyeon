package com.app.edonymyeon.presentation.ui.main.home.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.edonymyeon.R
import app.edonymyeon.databinding.ItemHotPostBinding
import com.app.edonymyeon.presentation.uimodel.PostItemUiModel
import com.app.edonymyeon.presentation.uimodel.ReactionCountUiModel

class HotPostViewHolder(parent: ViewGroup, onClick: (Int) -> Unit) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_hot_post,
            parent,
            false,
        ),
    ) {
    private val binding = ItemHotPostBinding.bind(itemView)

    init {
        binding.root.setOnClickListener {
            onClick(bindingAdapterPosition)
        }
    }

    fun bind(hotPost: PostItemUiModel) {
        binding.post = hotPost
        binding.prvHotPostReaction.reactionCount = ReactionCountUiModel(
            hotPost.reactionCount.viewCount,
            hotPost.reactionCount.commentCount,
            hotPost.reactionCount.scrapCount,
        )
    }
}
