package com.app.edonymyeon.presentation.ui.main.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.edonymyeon.presentation.ui.main.home.viewholder.AllPostViewHolder
import com.app.edonymyeon.presentation.uimodel.AllPostItemUiModel

class AllPostAdapter(
    private val allPosts: List<AllPostItemUiModel>,
    private val onClick: (Long) -> Unit,
) :
    RecyclerView.Adapter<AllPostViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllPostViewHolder {
        return AllPostViewHolder(parent, onClick = {
            onClick(allPosts[it].id)
        })
    }

    override fun getItemCount(): Int = allPosts.size

    override fun onBindViewHolder(holder: AllPostViewHolder, position: Int) {
        holder.bind(allPosts[position])
    }
}
