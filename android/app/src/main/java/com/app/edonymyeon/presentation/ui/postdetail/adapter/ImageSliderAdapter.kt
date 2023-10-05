package com.app.edonymyeon.presentation.ui.postdetail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.edonymyeon.presentation.ui.postdetail.viewholder.ImageSliderViewHolder

class ImageSliderAdapter(
    private val images: List<String>,
    private val onImageClick: (position: Int) -> Unit,
) : RecyclerView.Adapter<ImageSliderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSliderViewHolder {
        return ImageSliderViewHolder(parent, onImageClick)
    }

    override fun onBindViewHolder(holder: ImageSliderViewHolder, position: Int) {
        holder.bind(images[position], position)
    }

    override fun getItemCount(): Int = images.size
}
