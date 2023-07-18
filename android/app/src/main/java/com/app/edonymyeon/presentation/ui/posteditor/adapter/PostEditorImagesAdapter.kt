package com.app.edonymyeon.presentation.ui.posteditor.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.app.edonymyeon.presentation.ui.posteditor.viewholder.PostEditorImagesViewHolder

class PostEditorImagesAdapter(
    private val deleteImage: (String) -> Unit,
) : ListAdapter<String, PostEditorImagesViewHolder>(ImageDiffUtilCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostEditorImagesViewHolder {
        return PostEditorImagesViewHolder(parent)
    }

    override fun onBindViewHolder(holder: PostEditorImagesViewHolder, position: Int) {
        holder.bind(currentList[position], deleteImage)
    }
}
