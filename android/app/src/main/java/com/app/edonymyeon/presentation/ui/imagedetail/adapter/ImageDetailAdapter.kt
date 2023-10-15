package com.app.edonymyeon.presentation.ui.imagedetail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.edonymyeon.presentation.ui.imagedetail.viewholder.ImageDetailViewHolder

class ImageDetailAdapter(
    private val images: List<String>,
) : RecyclerView.Adapter<ImageDetailViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageDetailViewHolder {
        return ImageDetailViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ImageDetailViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size
}
