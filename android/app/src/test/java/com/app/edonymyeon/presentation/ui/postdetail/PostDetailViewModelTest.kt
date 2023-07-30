package com.app.edonymyeon.presentation.ui.postdetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.app.edonymyeon.mapper.toUiModel
import com.domain.edonymyeon.model.Count
import com.domain.edonymyeon.model.Post
import com.domain.edonymyeon.model.ReactionCount
import com.domain.edonymyeon.model.Recommendation
import com.domain.edonymyeon.model.Writer
import com.domain.edonymyeon.repository.PostRepository
import com.domain.edonymyeon.repository.RecommendRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class PostDetailViewModelTest() {

    private lateinit var viewModel: PostDetailViewModel
    private lateinit var postRepository: PostRepository
    private lateinit var recommendRepository: RecommendRepository

    private val postId = 1L
    private val fakePost = Post(
        id = postId,
        title = "title",
        price = 10000,
        content = "asdf",
        images = listOf(),
        createdAt = LocalDateTime.now(),
        writer = Writer(
            id = 1,
            nickname = "Hattti",
            profileImage = null,
        ),
        reactionCount = ReactionCount(
            viewCount = Count(3),
            commentCount = Count(0),
            scrapCount = Count(0),
        ),
        recommendation = Recommendation(
            upCount = Count(12),
            downCount = Count(12),
            isUp = false,
            isDown = false,
        ),
        isScrap = false,
        isWriter = false,
    )

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        postRepository = mockk()
        recommendRepository = mockk()
        viewModel = PostDetailViewModel(postRepository, recommendRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `post를 불러온다`() {
        // given
        coEvery { postRepository.getPostDetail(postId) } returns Result.success(fakePost)

        // when
        viewModel.getPostDetail(postId)

        // then
        assertEquals(fakePost.toUiModel(), viewModel.post.value)
    }

    @Test
    fun `추천 비추천을 하지 않은 상태에서 추천을 누른다`() {
        // given
        val post = fakePost.copy(
            recommendation = Recommendation(
                upCount = Count(12),
                downCount = Count(12),
                isUp = false,
                isDown = false,
            ),
        )
        coEvery { postRepository.getPostDetail(postId) } returns Result.success(post)
        coEvery { recommendRepository.saveRecommendUp(postId) } returns Result.success(Unit)

        // when
        viewModel.getPostDetail(postId)
        viewModel.updateUpRecommendationUi(postId, true)

        // then
        val actual = Recommendation(
            upCount = Count(13),
            downCount = Count(12),
            isUp = true,
            isDown = false,
        ).toUiModel()
        assertEquals(actual, viewModel.recommendation.value)
    }

    @Test
    fun `추천 비추천을 하지 않은 상태에서 비추천을 누른다`() {
        // given
        val post = fakePost.copy(
            recommendation = Recommendation(
                upCount = Count(12),
                downCount = Count(12),
                isUp = false,
                isDown = false,
            ),
        )
        coEvery { postRepository.getPostDetail(postId) } returns Result.success(post)
        coEvery { recommendRepository.saveRecommendDown(postId) } returns Result.success(Unit)

        // when
        viewModel.getPostDetail(postId)
        viewModel.updateDownRecommendationUi(postId, true)

        // then
        val actual = Recommendation(
            upCount = Count(12),
            downCount = Count(13),
            isUp = false,
            isDown = true,
        ).toUiModel()
        assertEquals(actual, viewModel.recommendation.value)
    }

    @Test
    fun `추천한 상태에서 추천을 누른다`() {
        // given
        val post = fakePost.copy(
            recommendation = Recommendation(
                upCount = Count(12),
                downCount = Count(12),
                isUp = true,
                isDown = false,
            ),
        )
        coEvery { postRepository.getPostDetail(postId) } returns Result.success(post)
        coEvery { recommendRepository.deleteRecommendUp(postId) } returns Result.success(Unit)

        // when
        viewModel.getPostDetail(postId)
        viewModel.updateUpRecommendationUi(postId, false)

        // then
        val actual = Recommendation(
            upCount = Count(11),
            downCount = Count(12),
            isUp = false,
            isDown = false,
        ).toUiModel()
        assertEquals(actual, viewModel.recommendation.value)
    }

    @Test
    fun `비추천한 상태에서 추천을 누른다`() {
        // given
        val post = fakePost.copy(
            recommendation = Recommendation(
                upCount = Count(12),
                downCount = Count(12),
                isUp = false,
                isDown = true,
            ),
        )
        coEvery { postRepository.getPostDetail(postId) } returns Result.success(post)
        coEvery { recommendRepository.saveRecommendUp(postId) } returns Result.success(Unit)

        // when
        viewModel.getPostDetail(postId)
        viewModel.updateUpRecommendationUi(postId, true)
        viewModel.updateDownRecommendationUi(postId, false)

        // then
        val actual = Recommendation(
            upCount = Count(13),
            downCount = Count(11),
            isUp = true,
            isDown = false,
        ).toUiModel()
        assertEquals(actual, viewModel.recommendation.value)
    }

    @Test
    fun `비추천한 상태에서 비추천을 누른다`() {
        // given
        val post = fakePost.copy(
            recommendation = Recommendation(
                upCount = Count(12),
                downCount = Count(12),
                isUp = false,
                isDown = true,
            ),
        )
        coEvery { postRepository.getPostDetail(postId) } returns Result.success(post)
        coEvery { recommendRepository.deleteRecommendDown(postId) } returns Result.success(Unit)

        // when
        viewModel.getPostDetail(postId)
        viewModel.updateDownRecommendationUi(postId, false)

        // then
        val actual = Recommendation(
            upCount = Count(12),
            downCount = Count(11),
            isUp = false,
            isDown = false,
        ).toUiModel()
        assertEquals(actual, viewModel.recommendation.value)
    }

    @Test
    fun `추천한 상태에서 비추천을 누른다`() {
        // given
        val post = fakePost.copy(
            recommendation = Recommendation(
                upCount = Count(12),
                downCount = Count(12),
                isUp = true,
                isDown = false,
            ),
        )
        coEvery { postRepository.getPostDetail(postId) } returns Result.success(post)
        coEvery { recommendRepository.saveRecommendDown(postId) } returns Result.success(Unit)

        // when
        viewModel.getPostDetail(postId)
        viewModel.updateUpRecommendationUi(postId, false)
        viewModel.updateDownRecommendationUi(postId, true)

        // then
        val actual = Recommendation(
            upCount = Count(11),
            downCount = Count(13),
            isUp = false,
            isDown = true,
        ).toUiModel()
        assertEquals(actual, viewModel.recommendation.value)
    }
}
