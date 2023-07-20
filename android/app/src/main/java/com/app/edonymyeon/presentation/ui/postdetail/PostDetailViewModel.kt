package com.app.edonymyeon.presentation.ui.postdetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.mapper.toDomain
import com.app.edonymyeon.mapper.toUiModel
import com.app.edonymyeon.presentation.uimodel.PostUiModel
import com.app.edonymyeon.presentation.uimodel.ReactionCountUiModel
import com.app.edonymyeon.presentation.uimodel.RecommendationUiModel
import com.domain.edonymyeon.model.Post
import com.domain.edonymyeon.repository.PostRepository
import kotlinx.coroutines.launch

class PostDetailViewModel(postId: Long, private val repository: PostRepository) : ViewModel() {
    private val _post = MutableLiveData<PostUiModel>()
    val post: LiveData<PostUiModel>
        get() = _post

    private val _recommendation = MutableLiveData<RecommendationUiModel>()
    val recommendation: LiveData<RecommendationUiModel>
        get() = _recommendation

    private val _reactionCount = MutableLiveData<ReactionCountUiModel>()
    val reactionCount: LiveData<ReactionCountUiModel>
        get() = _reactionCount

    private val _isScrap = MutableLiveData<Boolean>()
    val isScrap: LiveData<Boolean>
        get() = _isScrap

    init {
        getPostDetail(postId)
    }

    private fun getPostDetail(postId: Long) {
        Log.d("Test", "Get Post Start")
        viewModelScope.launch {
            repository.getPostDetail(postId)
                .onSuccess {
                    it as Post
                    _recommendation.value = it.recommendation.toUiModel()
                    _reactionCount.value = it.reactionCount.toUiModel()
                    _isScrap.value = it.isScrap
                    _post.value = it.toUiModel()
                }.onFailure {
                    it as CustomThrowable
                    when (it.code) {
                    }
                }
        }
    }

    fun updateUpRecommendation(isChecked: Boolean) {
        val oldRecommendation = _recommendation.value?.toDomain() ?: return
        if (oldRecommendation.isUp && isChecked) return

        if (isChecked) {
            if (oldRecommendation.isDown) {
                _recommendation.value = oldRecommendation.copy(
                    upCount = oldRecommendation.upCount.increase(),
                    isUp = true,
                    isDown = false,
                ).toUiModel()
            } else {
                _recommendation.value = oldRecommendation.copy(
                    upCount = oldRecommendation.upCount.increase(),
                    isUp = true,
                ).toUiModel()
            }
        } else {
            _recommendation.value = oldRecommendation.copy(
                upCount = oldRecommendation.upCount.decrease(),
                isUp = false,
            ).toUiModel()
        }
    }

    fun updateDownRecommendation(isChecked: Boolean) {
        val oldRecommendation = _recommendation.value?.toDomain() ?: return
        if (oldRecommendation.isDown && isChecked) return

        if (isChecked) {
            if (oldRecommendation.isUp) {
                _recommendation.value = oldRecommendation.copy(
                    downCount = oldRecommendation.downCount.increase(),
                    isUp = false,
                    isDown = true,
                ).toUiModel()
            } else {
                _recommendation.value = oldRecommendation.copy(
                    downCount = oldRecommendation.downCount.increase(),
                    isDown = true,
                ).toUiModel()
            }
        } else {
            _recommendation.value = oldRecommendation.copy(
                downCount = oldRecommendation.downCount.decrease(),
                isDown = false,
            ).toUiModel()
        }
    }

    fun updateScrap(isChecked: Boolean) {
        if (_post.value?.isWriter == true) return
        if (_isScrap.value == true && isChecked) return
        val oldReactionCount = _reactionCount.value?.toDomain() ?: return

        _isScrap.value = isChecked
        _reactionCount.value = if (isChecked) {
            oldReactionCount.copy(
                scrapCount = oldReactionCount.scrapCount.increase(),
            ).toUiModel()
        } else {
            oldReactionCount.copy(
                scrapCount = oldReactionCount.scrapCount.decrease(),
            ).toUiModel()
        }
    }

    fun deletePost() {
    }
}
