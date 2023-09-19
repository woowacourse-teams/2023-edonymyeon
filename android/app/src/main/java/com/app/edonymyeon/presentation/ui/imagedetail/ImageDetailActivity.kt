package com.app.edonymyeon.presentation.ui.imagedetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import app.edonymyeon.R
import app.edonymyeon.databinding.ActivityImageDetailBinding
import com.app.edonymyeon.presentation.ui.imagedetail.adapter.ImageDetailAdapter
import com.app.edonymyeon.presentation.uimodel.CommentUiModel
import com.app.edonymyeon.presentation.uimodel.PostUiModel
import com.app.edonymyeon.presentation.util.getParcelableExtraCompat

class ImageDetailActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityImageDetailBinding.inflate(layoutInflater)
    }

    private val post by lazy {
        intent.getParcelableExtraCompat(KEY_POST) as? PostUiModel
    }

    private val comment by lazy {
        intent.getParcelableExtraCompat(KEY_COMMENT) as? CommentUiModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        processImageData(post)
        processImageData(comment)
        setListener()
    }

    private fun processImageData(data: Parcelable?) {
        data?.let {
            setImageSlider(it)
            setImageIndicators()
        }
    }

    private fun setListener() {
        binding.ivClose.setOnClickListener {
            finish()
        }
    }

    private fun setImageSlider(data: Parcelable) {
        binding.vpImageSlider.offscreenPageLimit = 1
        binding.vpImageSlider.adapter = when (data) {
            is PostUiModel -> ImageDetailAdapter(data.images)
            is CommentUiModel -> {
                data.image?.let {
                    ImageDetailAdapter(listOf(it))
                }
            }

            else -> throw IllegalArgumentException("Unexpected data type: $data")
        }
        binding.vpImageSlider.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateCurrentIndicator(position)
                }
            },
        )
    }

    private fun setImageIndicators() {
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        ).apply { setMargins(8, 0, 8, 0) }

        addIndicatorViews(params)
        updateCurrentIndicator(0)
    }

    private fun addIndicatorViews(params: LinearLayout.LayoutParams) {
        List<ImageView>(binding.vpImageSlider.adapter?.itemCount ?: 0) {
            ImageView(this).apply {
                setImageResource(R.drawable.ic_bcc4d8_indicator_focus_off)
                layoutParams = params
            }.also { indicatorView ->
                binding.llIndicators.addView(indicatorView)
            }
        }
    }

    private fun updateCurrentIndicator(position: Int) {
        for (i in 0 until binding.llIndicators.childCount) {
            val indicatorView = binding.llIndicators.getChildAt(i) as ImageView
            if (i == position) {
                indicatorView.setImageResource(R.drawable.ic_576b9e_indicator_focus_on)
            } else {
                indicatorView.setImageResource(R.drawable.ic_bcc4d8_indicator_focus_off)
            }
        }
    }

    companion object {

        private const val KEY_POST = "key_post"
        private const val KEY_POSITION = "key_position"
        private const val KEY_COMMENT = "key_comment"

        fun newIntent(context: Context, post: PostUiModel, position: Int): Intent {
            return Intent(context, ImageDetailActivity::class.java).apply {
                putExtra(KEY_POST, post)
                putExtra(KEY_POSITION, position)
            }
        }

        fun newIntent(context: Context, comment: CommentUiModel): Intent {
            return Intent(context, ImageDetailActivity::class.java).putExtra(KEY_COMMENT, comment)
        }
    }
}
