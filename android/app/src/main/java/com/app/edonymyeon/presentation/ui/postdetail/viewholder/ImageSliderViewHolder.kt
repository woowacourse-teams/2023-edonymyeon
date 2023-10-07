package com.app.edonymyeon.presentation.ui.postdetail.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.edonymyeon.R
import app.edonymyeon.databinding.ItemImageSliderBinding

class ImageSliderViewHolder(
    parent: ViewGroup,
    private val onImageClick: (position: Int) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_image_slider, parent, false),
) {
    private val binding = ItemImageSliderBinding.bind(itemView)

    fun bind(imageUrl: String, position: Int) {
        binding.imageUrl = imageUrl
        binding.ivPostImageItem.setOnClickListener {
            onImageClick(position)
        }
    }
}
