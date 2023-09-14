package com.app.edonymyeon.presentation.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import app.edonymyeon.R
import app.edonymyeon.databinding.FragmentHomeBinding
import com.app.edonymyeon.data.datasource.post.PostRemoteDataSource
import com.app.edonymyeon.data.repository.PostRepositoryImpl
import com.app.edonymyeon.presentation.common.fragment.BaseFragment
import com.app.edonymyeon.presentation.ui.main.home.adapter.AllPostAdapter
import com.app.edonymyeon.presentation.ui.main.home.adapter.HotPostAdapter
import com.app.edonymyeon.presentation.ui.post.PostActivity
import com.app.edonymyeon.presentation.ui.postdetail.PostDetailActivity

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(
    {
        FragmentHomeBinding.inflate(it)
    },
) {

    override val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            PostRepositoryImpl(PostRemoteDataSource()),
        )
    }
    override val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }

    private val allPostAdapter by lazy {
        AllPostAdapter { id ->
            startActivity(PostDetailActivity.newIntent(requireContext(), id))
        }
    }

    private val hotPostAdapter by lazy {
        HotPostAdapter { id ->
            requireContext().startActivity(PostDetailActivity.newIntent(requireContext(), id))
        }
    }

    private var isIndicatorSet = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding.lifecycleOwner = this
        binding.homeViewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        setPosts()
        setAllPostAdapter()
        setHotPostAdapter()
        setListener()
    }

    override fun onResume() {
        super.onResume()
        refreshAndScrollToTop()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            refreshAndScrollToTop()
        }
    }

    private fun setObserver() {
        viewModel.allPosts.observe(viewLifecycleOwner) {
            allPostAdapter.setAllPosts(it)
        }
        viewModel.hotPosts.observe(viewLifecycleOwner) {
            binding.clHotPostNotContent.isVisible = it.isEmpty()
            hotPostAdapter.setHotPosts(it)
            if (!isIndicatorSet) {
                setImageIndicators()
                isIndicatorSet = true
            }
        }
    }

    private fun setPosts() {
        viewModel.getAllPosts()
        viewModel.getHotPosts()
    }

    private fun setAllPostAdapter() {
        binding.rvAllPost.adapter = allPostAdapter
    }

    private fun setHotPostAdapter() {
        binding.vpHotPost.offscreenPageLimit = 1
        binding.vpHotPost.adapter = hotPostAdapter
        binding.vpHotPost.registerOnPageChangeCallback(
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
        List(binding.vpHotPost.adapter?.itemCount ?: 0) {
            ImageView(requireContext()).apply {
                setImageResource(R.drawable.ic_bfc3ce_indicator_focus_off)
                layoutParams = params
            }.also { indicatorView ->
                binding.llIndicators.addView(indicatorView)
            }
        }
    }

    private fun updateCurrentIndicator(position: Int) {
        (0 until binding.llIndicators.childCount).forEach { i ->
            val indicatorView = binding.llIndicators.getChildAt(i) as ImageView
            if (i == position) {
                indicatorView.setImageResource(R.drawable.ic_ffffff_indicator_focus_on)
            } else {
                indicatorView.setImageResource(R.drawable.ic_bfc3ce_indicator_focus_off)
            }
        }
    }

    private fun setListener() {
        binding.ivAllPostMore.setOnClickListener {
            startActivity(PostActivity.newIntent(requireContext()))
        }
    }

    private fun refreshAndScrollToTop() {
        setPosts()
        viewModel.allPostSuccess.observe(viewLifecycleOwner) {
            binding.rvAllPost.smoothScrollToPosition(TOP_POSITION)
        }
    }

    companion object {
        private const val TOP_POSITION = 0
    }
}
