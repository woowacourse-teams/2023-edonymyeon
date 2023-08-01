package com.app.edonymyeon.presentation.ui.mypost.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.app.edonymyeon.presentation.ui.mypost.listener.MyPostClickListener
import com.app.edonymyeon.presentation.ui.mypost.viewholder.MyPostViewHolder
import com.app.edonymyeon.presentation.uimodel.MyPostUiModel

class MyPostAdapter(
    private val clickListener: MyPostClickListener,
) : ListAdapter<MyPostUiModel, MyPostViewHolder>(MyPostDiffUtilCallback) {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostViewHolder {
        return MyPostViewHolder(parent, clickListener)
    }

    override fun onBindViewHolder(holder: MyPostViewHolder, position: Int) {
        return holder.bind(currentList[position])
    }

    fun setMyPosts(posts: List<MyPostUiModel>) {
        submitList(posts)
    }
}
