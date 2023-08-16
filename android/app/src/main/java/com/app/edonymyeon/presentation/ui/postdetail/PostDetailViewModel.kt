package com.app.edonymyeon.presentation.ui.postdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource
import com.app.edonymyeon.data.util.PreferenceUtil
import com.app.edonymyeon.mapper.toDomain
import com.app.edonymyeon.mapper.toUiModel
import com.app.edonymyeon.presentation.uimodel.PostUiModel
import com.app.edonymyeon.presentation.uimodel.ReactionCountUiModel
import com.app.edonymyeon.presentation.uimodel.RecommendationUiModel
import com.domain.edonymyeon.model.Post
import com.domain.edonymyeon.model.Recommendation
import com.domain.edonymyeon.repository.PostRepository
import com.domain.edonymyeon.repository.RecommendRepository
import com.domain.edonymyeon.repository.ReportRepository
import kotlinx.coroutines.launch

class PostDetailViewModel(
    private val postRepository: PostRepository,
    private val recommendRepository: RecommendRepository,
    private val reportRepository: ReportRepository,
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

    val isLogin: Boolean
        get() = PreferenceUtil.getValue(AuthLocalDataSource.USER_ACCESS_TOKEN) != null

    private val _isLoadingSuccess = MutableLiveData<Boolean>(false)
    val isLoadingSuccess: LiveData<Boolean>
        get() = _isLoadingSuccess

    fun getPostDetail(postId: Long) {
        viewModelScope.launch {
            postRepository.getPostDetail(postId)
                .onSuccess {
                    it as Post
                    _recommendation.value = it.recommendation.toUiModel()
                    _reactionCount.value = it.reactionCount.toUiModel()
                    _isScrap.value = it.isScrap
                    _post.value = it.toUiModel()
                    _isLoadingSuccess.value = true
                }.onFailure {
                    it as CustomThrowable
                    _isLoadingSuccess.value = false
                }
        }
    }

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            postRepository.deletePost(postId)
                .onSuccess {}
                .onFailure {
                    it as CustomThrowable
                }
        }
    }

    fun postReport(postId: Long, reportId: Int, content: String?) {
        viewModelScope.launch {
            reportRepository.postReport(postId, reportId, content)
                .onSuccess { }
                .onFailure {
                    it as CustomThrowable
                }
        }
    }

    fun updateRecommendationUi(postId: Long, isChecked: Boolean, isUpRecommendation: Boolean) {
        val oldRecommendation = _recommendation.value?.toDomain() ?: return
        // 기존에 추천/비추천 되어 있으면 그대로 리턴
        if (isRecommendationInitialSetting(isUpRecommendation, isChecked, oldRecommendation)) return

        _recommendation.value = when {
            // 추천
            isChecked && isUpRecommendation -> {
                oldRecommendation.copy(
                    upCount = oldRecommendation.upCount.increase(),
                    isUp = true,
                    isDown = false,
                )
            }

            // 비추천
            isChecked && !isUpRecommendation -> {
                oldRecommendation.copy(
                    downCount = oldRecommendation.downCount.increase(),
                    isUp = false,
                    isDown = true,
                )
            }

            // 추천 취소
            !isChecked && isUpRecommendation -> {
                oldRecommendation.copy(
                    upCount = oldRecommendation.upCount.decrease(),
                    isUp = false,
                )
            }

            // 비추천 취소
            else -> {
                oldRecommendation.copy(
                    downCount = oldRecommendation.downCount.decrease(),
                    isDown = false,
                )
            }
        }.toUiModel()

        // 간접적으로 바뀐 경우 (e.g. 추천할 때 기존 비추천이 취소되는 경우) API 호출 없이 리턴
        if (isIndirectChange(isUpRecommendation, isChecked, oldRecommendation)) return

        _isRecommendationRequestDone.value = false

        if (isUpRecommendation) {
            saveRecommendation(
                postId,
                if (isChecked) RecommendRepository::saveRecommendUp else RecommendRepository::deleteRecommendUp,
            )
        } else {
            saveRecommendation(
                postId,
                if (isChecked) RecommendRepository::saveRecommendDown else RecommendRepository::deleteRecommendDown,
            )
        }
    }

    private fun isRecommendationInitialSetting(
        isUpRecommendation: Boolean,
        isChecked: Boolean,
        oldRecommendation: Recommendation,
    ): Boolean {
        if (isUpRecommendation && isChecked && oldRecommendation.isUp) return true
        if (!isUpRecommendation && isChecked && oldRecommendation.isDown) return true
        return false
    }

    private fun isIndirectChange(
        isUpRecommendation: Boolean,
        isChecked: Boolean,
        oldRecommendation: Recommendation,
    ): Boolean {
        if (isUpRecommendation && isChecked == oldRecommendation.isUp) return true
        if (!isUpRecommendation && isChecked == oldRecommendation.isDown) return true
        return false
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
