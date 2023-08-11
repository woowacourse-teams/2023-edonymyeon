package com.app.edonymyeon.presentation.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
    ): View {
        binding.lifecycleOwner = this
        binding.homeViewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        viewModel.getAllPosts()
        binding.ivAllPostMore.setOnClickListener {
            startActivity(PostActivity.newIntent(requireContext()))
        }
    }

    private fun setObserver() {
        viewModel.allPosts.observe(viewLifecycleOwner) {
            setAllPostAdapter(it)
        }
    }

    private fun setAllPostAdapter(it: List<AllPostItemUiModel>) {
        binding.rvAllPost.adapter = AllPostAdapter(it) { id ->
            PostDetailActivity.newIntent(requireContext(), id)
        }
    }
}
