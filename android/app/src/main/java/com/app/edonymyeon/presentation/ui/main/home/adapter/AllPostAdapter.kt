package com.app.edonymyeon.presentation.ui.main.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.app.edonymyeon.presentation.ui.main.home.viewholder.AllPostViewHolder
import com.app.edonymyeon.presentation.uimodel.AllPostItemUiModel

class AllPostAdapter(
    private val onClick: (Long) -> Unit,
) : ListAdapter<AllPostItemUiModel, AllPostViewHolder>(AllPostDiffUtilCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllPostViewHolder {
        return AllPostViewHolder(parent, onClick = {
            onClick(currentList[it].id)
        })
    }

    override fun onBindViewHolder(holder: AllPostViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int {
        return super.getItemCount().coerceAtMost(MAX_ITEM_COUNT)
    }

    fun setAllPosts(allPosts: List<AllPostItemUiModel>) {
        submitList(allPosts)
    }

    companion object {
        private const val MAX_ITEM_COUNT = 5
    }
}
