package com.app.edonymyeon.presentation.ui.mypost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.domain.edonymyeon.repository.PostRepository
import com.domain.edonymyeon.repository.ProfileRepository

class MyPostViewModelFactory(private val profileRepository: ProfileRepository, private val postRepository: PostRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyPostViewModel(profileRepository, postRepository) as T
    }
}
