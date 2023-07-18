package com.app.edonymyeon.presentation.common.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import app.edonymyeon.databinding.ViewPostReactionBinding
import com.app.edonymyeon.presentation.uimodel.ReactionCountUiModel

class PostReactionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding = ViewPostReactionBinding.inflate(LayoutInflater.from(context), this, true)

    var reactionCount: ReactionCountUiModel = ReactionCountUiModel(0, 0, 0)
        set(value) {
            field = value
            binding.tvViewCount.text = value.viewCount.toString()
            binding.tvCommentCount.text = value.commentCount.toString()
            binding.tvScrapCount.text = value.scrapCount.toString()
        }
        get() = ReactionCountUiModel(
            binding.tvViewCount.text.toString().toInt(),
            binding.tvCommentCount.text.toString().toInt(),
            binding.tvScrapCount.text.toString().toInt(),
        )
}
