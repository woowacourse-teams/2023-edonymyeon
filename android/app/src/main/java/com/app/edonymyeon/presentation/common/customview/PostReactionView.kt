package com.app.edonymyeon.presentation.common.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import app.edonymyeon.databinding.ViewPostReactionBinding

class PostReactionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {
    val binding = ViewPostReactionBinding.inflate(LayoutInflater.from(context), this, true)
}
