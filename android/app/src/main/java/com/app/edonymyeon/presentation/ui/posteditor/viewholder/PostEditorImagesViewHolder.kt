package com.app.edonymyeon.presentation.ui.posteditor.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.edonymyeon.databinding.ItemPostEditorImageBinding

class PostEditorImagesViewHolder private constructor(
    private val binding: ItemPostEditorImageBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(imageUrl: String, deleteImages: (String) -> Unit) {
        binding.postEditorImage = imageUrl
        binding.ivPostGalleryImageRemove.setOnClickListener {
            deleteImages.invoke(imageUrl)
        }
        binding.executePendingBindings()
    }

    companion object {
        fun from(
            parent: ViewGroup,
        ): PostEditorImagesViewHolder {
            val binding = ItemPostEditorImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
            return PostEditorImagesViewHolder(binding)
        }
    }
}
