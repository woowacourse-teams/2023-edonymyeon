package com.app.edonymyeon.presentation.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import app.edonymyeon.R
import app.edonymyeon.databinding.FragmentHomeBinding
import com.app.edonymyeon.data.datasource.post.PostRemoteDataSource
import com.app.edonymyeon.data.repository.PostRepositoryImpl
import com.app.edonymyeon.presentation.ui.main.home.adapter.AllPostAdapter
import com.app.edonymyeon.presentation.ui.main.home.adapter.HotPostAdapter
import com.app.edonymyeon.presentation.ui.post.PostActivity
import com.app.edonymyeon.presentation.ui.postdetail.PostDetailActivity
import com.app.edonymyeon.presentation.uimodel.AllPostItemUiModel

class HomeFragment : Fragment() {
    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private val viewModel: HomeViewModel by viewModels() {
        HomeViewModelFactory(PostRepositoryImpl(PostRemoteDataSource()))
    }

    private val hotPostAdapter by lazy {
        HotPostAdapter { id ->
            requireContext().startActivity(PostDetailActivity.newIntent(requireContext(), id))
        }
    }

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
        setHotPostAdapter()
        setListener()

        viewModel.getAllPosts()
        viewModel.getHotPosts()
    }

    private fun setObserver() {
        viewModel.allPosts.observe(viewLifecycleOwner) {
            setAllPostAdapter(it)
        }
        viewModel.hotPosts.observe(viewLifecycleOwner) {
            hotPostAdapter.setHotPosts(it)
            setImageIndicators()
        }
    }

    private fun setAllPostAdapter(it: List<AllPostItemUiModel>) {
        binding.rvAllPost.adapter = AllPostAdapter(it) { id ->
            PostDetailActivity.newIntent(requireContext(), id)
        }
    }

    private fun setListener() {
        binding.ivAllPostMore.setOnClickListener {
            startActivity(PostActivity.newIntent(requireContext()))
        }
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
}
