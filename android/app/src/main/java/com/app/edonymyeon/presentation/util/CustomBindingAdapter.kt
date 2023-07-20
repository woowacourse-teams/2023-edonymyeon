package com.app.edonymyeon.presentation.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import app.edonymyeon.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

object CustomBindingAdapter {

    @BindingAdapter("imgUrlCenterCrop")
    @JvmStatic
    fun setCenterCropImageResource(view: ImageView, url: String) {
        Glide.with(view.context)
            .load(url)
            .error(R.drawable.ic_launcher_foreground)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view)
    }

    @BindingAdapter("imgUrlFitCenter")
    @JvmStatic
    fun setFitCenterImageResource(view: ImageView, url: String) {
        Glide.with(view.context)
            .load(url)
            .error(R.drawable.ic_launcher_foreground)
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view)
    }

    @BindingAdapter("imgUrlCircleCrop")
    @JvmStatic
    fun setCircleCropImageResource(view: ImageView, url: String) {
        Glide.with(view.context)
            .load(url)
            .error(R.drawable.ic_launcher_foreground)
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view)
    }
}
