package com.app.edonymyeon.presentation.ui.postdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.domain.edonymyeon.repository.PostRepository
import com.domain.edonymyeon.repository.RecommendRepository
import com.domain.edonymyeon.repository.ReportRepository

class PostDetailViewModelFactory(
    private val postRepository: PostRepository,
    private val recommendRepository: RecommendRepository,
    private val reportRepository: ReportRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostDetailViewModel(postRepository, recommendRepository, reportRepository) as T
    }
}
