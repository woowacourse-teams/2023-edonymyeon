package com.app.edonymyeon.presentation.ui.post.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.edonymyeon.presentation.ui.post.viewholder.PostViewHolder
import com.app.edonymyeon.presentation.uimodel.PostItemUiModel

class PostAdapter(private val posts: List<PostItemUiModel>, private val onClick: (Long) -> Unit) :
    RecyclerView.Adapter<PostViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(parent, onClick = {
            onClick(posts[it].id)
        })
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }
}
