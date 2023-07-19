package com.app.edonymyeon.presentation.ui.posteditor.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.edonymyeon.R
import app.edonymyeon.databinding.ItemPostEditorImageBinding

class PostEditorImagesViewHolder(
    parent: ViewGroup,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.item_post_editor_image,
        parent,
        false,
    ),
) {
    private val binding = ItemPostEditorImageBinding.bind(itemView)

    fun bind(imageUrl: String, deleteImages: (String) -> Unit) {
        binding.postEditorImage = imageUrl
        binding.ivPostGalleryImageRemove.setOnClickListener {
            deleteImages.invoke(imageUrl)
        }
        binding.executePendingBindings()
    }
}
