package com.app.edonymyeon.presentation.ui.main.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.app.edonymyeon.presentation.common.diffutil.PostDiffUtilCallback
import com.app.edonymyeon.presentation.ui.main.home.viewholder.HotPostViewHolder
import com.app.edonymyeon.presentation.uimodel.PostItemUiModel

class HotPostAdapter(private val onClick: (Long) -> Unit) :
    ListAdapter<PostItemUiModel, HotPostViewHolder>(PostDiffUtilCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotPostViewHolder {
        return HotPostViewHolder(parent) {
            onClick(currentList[it].id)
        }
    }

    override fun onBindViewHolder(holder: HotPostViewHolder, position: Int) {
        return holder.bind(currentList[position])
    }

    fun setHotPosts(hotposts: List<PostItemUiModel>) {
        submitList(hotposts)
    }
}
