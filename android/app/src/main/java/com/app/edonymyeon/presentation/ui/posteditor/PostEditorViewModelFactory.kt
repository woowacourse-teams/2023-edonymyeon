package com.app.edonymyeon.presentation.ui.posteditor

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.domain.edonymyeon.repository.PostRepository

class PostEditorViewModelFactory(
    private val application: Application,
    private val repository: PostRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostEditorViewModel(application, repository) as T
    }
}
