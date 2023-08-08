package com.app.edonymyeon.presentation.ui.main.home.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.edonymyeon.R
import com.app.edonymyeon.databinding.ItemAllPostBinding
import com.app.edonymyeon.presentation.uimodel.AllPostItemUiModel

class AllPostViewHolder(parent: ViewGroup, onClick: (Int) -> Unit) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.item_all_post,
        parent,
        false,
    ),
) {
    private val binding = ItemAllPostBinding.bind(itemView)

    init {
        binding.root.setOnClickListener {
            onClick(bindingAdapterPosition)
        }
    }

    fun bind(allPostItem: AllPostItemUiModel) {
        binding.allPostItem = allPostItem
    }
}
