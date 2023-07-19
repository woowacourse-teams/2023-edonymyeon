package com.app.edonymyeon.presentation.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import app.edonymyeon.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

object CustomBindingAdapter {

    @BindingAdapter("imgResId")
    @JvmStatic
    fun setImageResource(view: ImageView, url: String) {
        Glide.with(view.context)
            .load(url)
            .error(R.drawable.ic_launcher_foreground)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view)
    }
}
