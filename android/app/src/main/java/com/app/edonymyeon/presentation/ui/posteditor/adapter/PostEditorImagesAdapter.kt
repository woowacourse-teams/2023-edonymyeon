package com.app.edonymyeon.presentation.ui.posteditor.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.app.edonymyeon.presentation.ui.posteditor.viewholder.PostEditorImagesViewHolder

class PostEditorImagesAdapter(
    private val deleteImage: (String) -> Unit,
) : ListAdapter<String, PostEditorImagesViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostEditorImagesViewHolder {
        return PostEditorImagesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PostEditorImagesViewHolder, position: Int) {
        holder.bind(currentList[position], deleteImage)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }
}
