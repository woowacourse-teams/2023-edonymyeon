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
import com.domain.edonymyeon.repository.RecommendRepository
import kotlinx.coroutines.launch

class PostDetailViewModel(
    private val postRepository: PostRepository,
    private val recommendRepository: RecommendRepository,
) : ViewModel() {

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

    private val _isRecommendationRequestDone = MutableLiveData<Boolean>(true)
    val isRecommendationRequestDone: LiveData<Boolean>
        get() = _isRecommendationRequestDone

    fun getPostDetail(postId: Long) {
        viewModelScope.launch {
            postRepository.getPostDetail(postId)
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

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            postRepository.deletePost(postId)
                .onSuccess {
                    Log.d("PostDetailViewModel", "deletePost: success")
                }
                .onFailure {
                    it as CustomThrowable
                    when (it.code) {
                    }
                }
        }
    }

    fun updateUpRecommendationUi(postId: Long, isChecked: Boolean) {
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

        if (oldRecommendation.isUp == isChecked) return

        _isRecommendationRequestDone.value = false

        if (isChecked) {
            saveRecommendation(postId, RecommendRepository::saveRecommendUp)
        } else {
            saveRecommendation(postId, RecommendRepository::deleteRecommendUp)
        }
    }

    fun updateDownRecommendationUi(postId: Long, isChecked: Boolean) {
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

        if (oldRecommendation.isDown == isChecked) return

        _isRecommendationRequestDone.value = false

        if (isChecked) {
            saveRecommendation(postId, RecommendRepository::saveRecommendDown)
        } else {
            saveRecommendation(postId, RecommendRepository::deleteRecommendDown)
        }
    }

    private fun saveRecommendation(
        postId: Long,
        event: suspend RecommendRepository.(Long) -> Result<Any>,
    ) {
        viewModelScope.launch {
            recommendRepository.event(postId)
                .onSuccess {
                    _isRecommendationRequestDone.value = true
                }
                .onFailure {
                    _isRecommendationRequestDone.value = true
                    it as CustomThrowable
                    when (it.code) {
                    }
                }
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
}
