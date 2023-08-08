package com.app.edonymyeon.presentation.ui.main.search.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.app.edonymyeon.presentation.ui.main.search.viewholder.SearchViewHolder
import com.app.edonymyeon.presentation.uimodel.PostItemUiModel

class SearchAdapter(private val onClick: (Long) -> Unit) :
    ListAdapter<PostItemUiModel, SearchViewHolder>(SearchDiffUtilCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(parent) {
            onClick(currentList[it].id)
        }
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(this.currentList[position])
    }

    fun setPosts(posts: List<PostItemUiModel>) {
        submitList(posts)
    }
}
