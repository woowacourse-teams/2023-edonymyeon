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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PostDetailViewModel(
    private val postId: Long,
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

    // 추천/비추천 요청 완료 여부 (기본은 true, 클릭하면 false, 응답을 받으면 다시 true)
    // TODO: 응답이 끝나지 않았는데 다른 페이지로 나갔다 오면 어떻게 될까요?
    private val _isRecommendationRequestDone = MutableLiveData<Boolean>(true)
    val isRecommendationRequestDone: LiveData<Boolean>
        get() = _isRecommendationRequestDone

    init {
        getPostDetail(postId)
    }

    private fun getPostDetail(postId: Long) {
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

    fun updateUpRecommendationUi(isChecked: Boolean) {
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

        // 클릭 후 서버 통신을 보내기 전 우선 false가 된다
        _isRecommendationRequestDone.value = false
        Log.d("PostDetailViewModel", "up clicked! request done: false")

        if (isChecked) {
            saveRecommendation(postId, RecommendRepository::saveRecommendUp)
        } else {
            saveRecommendation(postId, RecommendRepository::deleteRecommendUp)
        }
    }

    fun updateDownRecommendationUi(isChecked: Boolean) {
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

        // 클릭 후 서버 통신을 보내기 전 우선 false가 된다
        _isRecommendationRequestDone.value = false
        Log.d("PostDetailViewModel", "down clicked! request done: false")

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
        // 서버 통신 요청 보낸다
        Log.d("PostDetailViewModel", "saveRecommendation")
        viewModelScope.launch {
            recommendRepository.event(postId)
                .onSuccess {
                    // 응답이 도착했지만 테스트를 위해 3초 기다리기 추가했습니당
                    delay(3000L)
                    _isRecommendationRequestDone.value = true
                    Log.d("PostDetailViewModel", "onSuccess! request done: true")
                }
                .onFailure {
                    _isRecommendationRequestDone.value = true
                    Log.d("PostDetailViewModel", "onFailure! request done: true")
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
