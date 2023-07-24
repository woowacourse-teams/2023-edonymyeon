package com.app.edonymyeon.presentation.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import app.edonymyeon.databinding.FragmentHomeBinding
import com.app.edonymyeon.presentation.ui.main.home.adapter.AllPostAdapter
import com.app.edonymyeon.presentation.ui.post.PostActivity
import com.app.edonymyeon.presentation.ui.postdetail.PostDetailActivity
import com.app.edonymyeon.presentation.uimodel.AllPostItemUiModel

class HomeFragment : Fragment() {
    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding.lifecycleOwner = this
        binding.homeViewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserve()
        viewModel.getAllPosts()
        binding.ivAllPostMore.setOnClickListener {
            startActivity(PostActivity.newIntent(requireContext()))
        }
    }

    private fun initObserve() {
        viewModel.allPosts.observe(viewLifecycleOwner, Observer {
            initAllPostAdapter(it)
        })
    }

    private fun initAllPostAdapter(it: List<AllPostItemUiModel>) {
        binding.rvAllPost.adapter = AllPostAdapter(it) { id ->
            PostDetailActivity.newIntent(requireContext(), id)
        }
    }
}
