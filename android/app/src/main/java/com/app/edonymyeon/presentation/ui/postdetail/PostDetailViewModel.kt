package com.app.edonymyeon.presentation.ui.postdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.mapper.toDomain
import com.app.edonymyeon.mapper.toUiModel
import com.app.edonymyeon.presentation.uimodel.PostUiModel
import com.app.edonymyeon.presentation.uimodel.RecommendationUiModel
import com.domain.edonymyeon.model.Count
import com.domain.edonymyeon.model.Post
import com.domain.edonymyeon.model.ReactionCount
import com.domain.edonymyeon.model.Recommendation
import com.domain.edonymyeon.model.Writer
import com.domain.edonymyeon.repository.PostRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class PostDetailViewModel(private val repository: PostRepository) : ViewModel() {
    private val _post = MutableLiveData<PostUiModel>()
    val post: LiveData<PostUiModel>
        get() = _post

    private val _recommendation = MutableLiveData<RecommendationUiModel>()
    val recommendation: LiveData<RecommendationUiModel>
        get() = _recommendation

    private val _isScrap = MutableLiveData<Boolean>()
    val isScrap: LiveData<Boolean>
        get() = _isScrap

    init {
        /*        _post.value = postData.toUiModel()
                _recommendation.value = postData.recommendation.toUiModel()
                _isScrap.value = postData.isScrap*/
    }

    fun getPostDetail(postId: Long) {
        viewModelScope.launch {
            repository.getPostDetail(postId).onSuccess {
                it as Post
                _post.value = it.toUiModel()
                _recommendation.value = it.recommendation.toUiModel()
                _isScrap.value = it.isScrap
            }.onFailure {
                it as CustomThrowable
                when (it.code) {
                }
            }
        }
    }

    fun updateUpRecommendation(isChecked: Boolean) {
        val oldRecommendation = _recommendation.value?.toDomain() ?: return
        if (oldRecommendation.isUp == isChecked) return

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
        if (oldRecommendation.isDown == isChecked) return

        if (isChecked) {
            if (oldRecommendation.isUp) {
                _recommendation.value = oldRecommendation.copy(
                    downCount = oldRecommendation.downCount.decrease(),
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
        _isScrap.value = isChecked
    }
}

private val postData = Post(
    id = 1,
    title = "립스틱 살까 말까 고민 중인데..",
    price = 44100,
    content = "안녕하세요! 립스틱을 사야할지 말아야할지 고민이 되는데 어떻게 하는게 좋을까요?",
    images = listOf(
        "https://contents.lotteon.com/itemimage/_v135805/LE/12/04/64/86/36/_1/22/51/40/89/2/LE1204648636_1225140892_1.jpg/dims/resizef/720X720",
        "https://gdimg.gmarket.co.kr/1811472676/still/400?ver=1589887288",
    ),
    createdAt = LocalDateTime.of(2023, 7, 16, 20, 4),
    writer = Writer(
        id = 1,
        nickname = "하티",
        profileImage = "data:image/jpeg;base64,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",
    ),
    reactionCount = ReactionCount(
        viewCount = 123,
        commentCount = 41,
        scrapCount = 77,
    ),
    recommendation = Recommendation(
        upCount = Count(0),
        downCount = Count(0),
        isUp = false,
        isDown = true,
    ),
    isScrap = true,
    isWriter = false,
)
