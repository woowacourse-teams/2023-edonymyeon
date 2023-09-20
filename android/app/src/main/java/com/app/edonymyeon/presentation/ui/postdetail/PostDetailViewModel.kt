package com.app.edonymyeon.presentation.ui.postdetail

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource
import com.app.edonymyeon.data.util.PreferenceUtil
import com.app.edonymyeon.mapper.toDomain
import com.app.edonymyeon.mapper.toUiModel
import com.app.edonymyeon.presentation.common.imageutil.processAndAdjustImage
import com.app.edonymyeon.presentation.common.viewmodel.BaseViewModel
import com.app.edonymyeon.presentation.uimodel.CommentUiModel
import com.app.edonymyeon.presentation.uimodel.PostUiModel
import com.app.edonymyeon.presentation.uimodel.ReactionCountUiModel
import com.app.edonymyeon.presentation.uimodel.RecommendationUiModel
import com.domain.edonymyeon.model.Post
import com.domain.edonymyeon.model.Recommendation
import com.domain.edonymyeon.repository.PostRepository
import com.domain.edonymyeon.repository.RecommendRepository
import com.domain.edonymyeon.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val recommendRepository: RecommendRepository,
    private val reportRepository: ReportRepository,
) : BaseViewModel() {

    private val _post = MutableLiveData<PostUiModel>()
    val post: LiveData<PostUiModel>
        get() = _post

    private val _comments = MutableLiveData<List<CommentUiModel>>()
    val comments: LiveData<List<CommentUiModel>>
        get() = _comments

    private val _recommendation = MutableLiveData<RecommendationUiModel>()
    val recommendation: LiveData<RecommendationUiModel>
        get() = _recommendation

    private val _reactionCount = MutableLiveData<ReactionCountUiModel>()
    val reactionCount: LiveData<ReactionCountUiModel>
        get() = _reactionCount

    private val _isRecommendationRequestDone = MutableLiveData<Boolean>(true)
    val isRecommendationRequestDone: LiveData<Boolean>
        get() = _isRecommendationRequestDone

    val isLogin: Boolean
        get() = PreferenceUtil.getValue(AuthLocalDataSource.USER_ACCESS_TOKEN) != null

    private val _isLoadingSuccess = MutableLiveData(false)
    val isLoadingSuccess: LiveData<Boolean>
        get() = _isLoadingSuccess

    private val _isCommentSave = MutableLiveData(false)
    val isCommentSave: LiveData<Boolean>
        get() = _isCommentSave

    private val _commentImage = MutableLiveData<Uri?>()
    val commentImage: LiveData<Uri?>
        get() = _commentImage

    private val _isCommentsLoadingSuccess = MutableLiveData(false)
    val isCommentLoadingSuccess: LiveData<Boolean>
        get() = _isCommentsLoadingSuccess

    private val _isPostLoadingSuccess = MutableLiveData(false)

    private val _isCommentSaveSuccess = MutableLiveData(false)
    val isCommentSaveSuccess: LiveData<Boolean>
        get() = _isCommentSaveSuccess

    private val _reportSaveMessage = MutableLiveData<String>()
    val reportSaveMessage: LiveData<String>
        get() = _reportSaveMessage
    private val _isPostDeleted = MutableLiveData(false)
    val isPostDeleted: LiveData<Boolean>
        get() = _isPostDeleted

    fun getPostDetail(postId: Long, notificationId: Long) {
        viewModelScope.launch(exceptionHandler) {
            postRepository.getPostDetail(postId, notificationId)
                .onSuccess {
                    it as Post
                    _recommendation.value = it.recommendation.toUiModel()
                    _reactionCount.value = it.reactionCount.toUiModel()
                    _post.value = it.toUiModel()
                    _isPostLoadingSuccess.value = true
                    checkLoadingSuccess()
                }.onFailure {
                    val customThrowable = it as CustomThrowable
                    when (customThrowable.code) {
                        2000 -> {
                            _isPostDeleted.value = true
                            _isLoadingSuccess.value = true
                        }

                        else -> _isPostLoadingSuccess.value = false
                    }
                }
        }
    }

    fun deletePost(postId: Long) {
        viewModelScope.launch(exceptionHandler) {
            postRepository.deletePost(postId)
                .onSuccess {}
                .onFailure {
                    it as CustomThrowable
                }
        }
    }

    fun postReport(type: ReportType, postId: Long, reportId: Int, content: String?) {
        viewModelScope.launch(exceptionHandler) {
            reportRepository.postReport(type.toString(), postId, reportId, content)
                .onSuccess { }
                .onSuccess {
                    _reportSaveMessage.value = MESSAGE_REPORT_SUCCESS
                }
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
        viewModelScope.launch(exceptionHandler) {
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

    fun getComments(postId: Long) {
        viewModelScope.launch {
            postRepository.getComments(postId).onSuccess { comments ->
                _comments.value = comments.comments.map { it.toUiModel() }
                _reactionCount.value = _reactionCount.value?.takeIf { true }?.copy(
                    commentCount = comments.commentCount,
                ) ?: ReactionCountUiModel(0, comments.commentCount)
                _isCommentsLoadingSuccess.value = true
                checkLoadingSuccess()
            }.onFailure {
                _isCommentsLoadingSuccess.value = false
            }
        }
    }

    fun postComment(context: Context, postId: Long, uri: Uri?, content: String) {
        viewModelScope.launch {
            postRepository.postComment(
                postId,
                if (uri == null) null else processAndAdjustImage(context, uri),
                content,
            ).onSuccess {
                getComments(postId)
                _isCommentSaveSuccess.value = true
            }.onFailure {
                _isCommentSaveSuccess.value = false
            }
        }
    }

    fun deleteComment(postId: Long, commentId: Long) {
        viewModelScope.launch {
            postRepository.deleteComment(
                postId,
                commentId,
            ).onSuccess {
                getComments(postId)
            }
        }
    }

    fun checkCommentValidate(content: String) {
        _isCommentSave.value = content.isNotBlank()
    }

    fun setCommentImage(image: Uri?) {
        _commentImage.value = image
    }

    private fun checkLoadingSuccess() {
        _isLoadingSuccess.value =
            _isPostLoadingSuccess.value == true && _isCommentsLoadingSuccess.value == true
    }

    companion object {
        private const val MESSAGE_REPORT_SUCCESS = "신고가 접수되었습니다."
    }
}
