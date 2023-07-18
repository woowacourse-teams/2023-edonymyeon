package com.app.edonymyeon.presentation.ui.postdetail.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.edonymyeon.R
import app.edonymyeon.databinding.ItemImageSliderBinding
import com.bumptech.glide.Glide

class ImageSliderViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_image_slider, parent, false),
) {
    private val binding = ItemImageSliderBinding.bind(itemView)

    fun bind(imageUrl: String) {
        Glide.with(itemView.context)
            .load(imageUrl)
            .into(binding.ivPostImageItem)
    }
}
