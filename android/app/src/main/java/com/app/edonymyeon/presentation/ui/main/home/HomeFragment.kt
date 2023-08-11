package com.app.edonymyeon.presentation.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.edonymyeon.databinding.FragmentHomeBinding
import com.app.edonymyeon.data.datasource.post.PostRemoteDataSource
import com.app.edonymyeon.data.repository.PostRepositoryImpl
import com.app.edonymyeon.presentation.ui.main.home.adapter.AllPostAdapter
import com.app.edonymyeon.presentation.ui.post.PostActivity
import com.app.edonymyeon.presentation.ui.postdetail.PostDetailActivity

class HomeFragment : Fragment() {
    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            PostRepositoryImpl(PostRemoteDataSource()),
        )
    }

    private val allPostAdapter by lazy {
        AllPostAdapter { id ->
            startActivity(PostDetailActivity.newIntent(requireContext(), id))
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
        initObserve()
        initAllPostAdapter()
        initListener()
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

    private fun initObserve() {
        viewModel.allPosts.observe(viewLifecycleOwner) {
            allPostAdapter.setAllPosts(it)
        }
    }

    private fun initAllPostAdapter() {
        binding.rvAllPost.adapter = allPostAdapter
    }

    private fun initListener() {
        binding.ivAllPostMore.setOnClickListener {
            startActivity(PostActivity.newIntent(requireContext()))
        }
    }

    private fun refreshAndScrollToTop() {
        viewModel.getAllPosts()
        viewModel.allPostSuccess.observe(viewLifecycleOwner) {
            binding.rvAllPost.smoothScrollToPosition(TOP_POSITION)
        }
    }

    companion object {
        private const val TOP_POSITION = 0
    }
}
