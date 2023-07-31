package com.app.edonymyeon.presentation.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import app.edonymyeon.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

object CustomBindingAdapter {

    @BindingAdapter("imgUrlCenterCrop")
    @JvmStatic
    fun setCenterCropImageResource(view: ImageView, url: String?) {
        Glide.with(view.context)
            .load(url)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view)
    }

    @BindingAdapter("imgUrlFitCenter")
    @JvmStatic
    fun setFitCenterImageResource(view: ImageView, url: String?) {
        Glide.with(view.context)
            .load(url)
            .error(R.drawable.ic_launcher_foreground)
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view)
    }

    @BindingAdapter("imgUrlCircleCrop")
    @JvmStatic
    fun setCircleCropImageResource(view: ImageView, url: String?) {
        Glide.with(view.context)
            .load(url)
            .error(R.drawable.ic_launcher_foreground)
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view)
    }

    @BindingAdapter("onPasswordCheckTextChanged")
    @JvmStatic
    fun setOnPasswordCheckTextChanged(
        editText: EditText,
        onPasswordCheckTextChanged: OnPasswordCheckTextChanged?,
    ) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                onPasswordCheckTextChanged?.onTextChanged(s?.toString() ?: "")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        })
    }

    interface OnPasswordCheckTextChanged {
        fun onTextChanged(text: String)
    }
}
