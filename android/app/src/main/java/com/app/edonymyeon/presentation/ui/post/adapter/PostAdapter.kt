package com.app.edonymyeon.presentation.ui.post.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.app.edonymyeon.presentation.ui.post.viewholder.PostViewHolder
import com.app.edonymyeon.presentation.uimodel.PostItemUiModel

class PostAdapter(private val onClick: (Long) -> Unit) :
    ListAdapter<PostItemUiModel, PostViewHolder>(PostDiffUtilCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(parent) {
            onClick(currentList[it].id)
        }
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(this.currentList[position])
    }

    fun setPosts(posts: List<PostItemUiModel>) {
        submitList(posts)
    }
}
