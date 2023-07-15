package com.app.edonymyeon.presentation.ui.posteditor.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.edonymyeon.databinding.ItemPostEditorImageBinding

class PostEditorImagesViewHolder private constructor(
    private val binding: ItemPostEditorImageBinding,
    private val deleteImage: (String) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(imageUrl: String) {
        binding.postEditorImage = imageUrl
        binding.ivPostGalleryImageRemove.setOnClickListener {
            deleteImage.invoke(imageUrl)
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            deleteImage: (String) -> Unit,
        ): PostEditorImagesViewHolder {
            val binding = ItemPostEditorImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
            return PostEditorImagesViewHolder(binding, deleteImage)
        }
    }
}
