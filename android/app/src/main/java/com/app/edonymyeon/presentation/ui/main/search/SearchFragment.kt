package com.app.edonymyeon.presentation.ui.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import app.edonymyeon.databinding.FragmentSearchBinding
import com.app.edonymyeon.data.datasource.search.SearchRemoteDataSource
import com.app.edonymyeon.data.repository.SearchRepositoryImpl
import com.app.edonymyeon.presentation.ui.main.search.adapter.SearchAdapter
import com.app.edonymyeon.presentation.ui.postdetail.PostDetailActivity

class SearchFragment : Fragment() {
    private val binding: FragmentSearchBinding by lazy {
        FragmentSearchBinding.inflate(layoutInflater)
    }

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelFactory(SearchRepositoryImpl(SearchRemoteDataSource()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSearchAdapter()
        setQuerySearchListener()
        setResultScrollListener()
        binding.searchView.setOnClickListener {
        }
    }

    private fun setResultScrollListener() {
        binding.rvSearchResult.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (viewModel.hasNextPage()) {
                    return
                }
                if (!binding.rvSearchResult.canScrollVertically(1)) {
                    viewModel.getSearchResult(binding.searchView.query.toString())
                }
            }
        })
    }

    private fun setSearchAdapter() {
        val searchAdapter = SearchAdapter(onClick = {
            startActivity(PostDetailActivity.newIntent(requireContext(), it))
        })

        binding.rvSearchResult.adapter = searchAdapter
        viewModel.searchResult.observe(viewLifecycleOwner) {
            searchAdapter.setPosts(it)
        }
    }

    private fun setQuerySearchListener() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.clearResult()
                viewModel.getSearchResult(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }
}
