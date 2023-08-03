package com.app.edonymyeon.presentation.ui.main.search.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.edonymyeon.R
import app.edonymyeon.databinding.ItemPostBinding
import com.app.edonymyeon.presentation.uimodel.PostItemUiModel

class SearchViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false),
) {
    private val binding = ItemPostBinding.bind(itemView)

    fun bind(postUiModel: PostItemUiModel) {
        binding.post = postUiModel
        binding.executePendingBindings()
    }
}
