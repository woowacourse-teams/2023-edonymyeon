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
import com.domain.edonymyeon.repository.ReportRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
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
    private lateinit var reportRepository: ReportRepository

    private val postId = 1L
    private val notificationId = 1L
    private fun createPost(
        id: Long = postId,
        title: String = "title",
        price: Int = 10000,
        content: String = "asdf",
        images: List<String> = listOf(),
        createdAt: LocalDateTime = LocalDateTime.now(),
        writer: Writer = Writer(
            id = 1,
            nickname = "Hattti",
            profileImage = null,
        ),
        reactionCount: ReactionCount = ReactionCount(
            viewCount = Count(3),
            commentCount = Count(0),
        ),
        recommendation: Recommendation,
        isWriter: Boolean = false,
    ) = Post(
        id,
        title,
        price,
        content,
        images,
        createdAt,
        writer,
        reactionCount,
        recommendation,
        isWriter,
    )

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        postRepository = mockk()
        recommendRepository = mockk()
        reportRepository = mockk()
        viewModel = PostDetailViewModel(postRepository, recommendRepository, reportRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `post를 불러온다`() {
        // given
        val post = createPost(
            recommendation = Recommendation(
                upCount = Count(12),
                downCount = Count(12),
                isUp = false,
                isDown = false,
            ),
        )
        coEvery {
            postRepository.getPostDetail(
                postId,
                notificationId,
            )
        } returns Result.success(post)

        // when
        viewModel.getPostDetail(postId, notificationId)

        // then
        assertEquals(post.toUiModel(), viewModel.post.value)
    }

    @Test
    fun `추천 비추천을 하지 않은 상태에서 추천을 누른다`() {
        // given
        val post = createPost(
            recommendation = Recommendation(
                upCount = Count(12),
                downCount = Count(12),
                isUp = false,
                isDown = false,
            ),
        )
        coEvery {
            postRepository.getPostDetail(
                postId,
                notificationId,
            )
        } returns Result.success(post)
        coEvery { recommendRepository.saveRecommendUp(postId) } returns Result.success(Unit)

        // when
        viewModel.getPostDetail(postId, notificationId)
        viewModel.updateRecommendationUi(
            postId = postId,
            isChecked = true,
            isUpRecommendation = true,
        )

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
        val post = createPost(
            recommendation = Recommendation(
                upCount = Count(12),
                downCount = Count(12),
                isUp = false,
                isDown = false,
            ),
        )
        coEvery {
            postRepository.getPostDetail(
                postId,
                notificationId,
            )
        } returns Result.success(post)
        coEvery { recommendRepository.saveRecommendDown(postId) } returns Result.success(Unit)

        // when
        viewModel.getPostDetail(postId, notificationId)
        viewModel.updateRecommendationUi(
            postId = postId,
            isChecked = true,
            isUpRecommendation = false,
        )

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
        val post = createPost(
            recommendation = Recommendation(
                upCount = Count(12),
                downCount = Count(12),
                isUp = true,
                isDown = false,
            ),
        )
        coEvery {
            postRepository.getPostDetail(
                postId,
                notificationId,
            )
        } returns Result.success(post)
        coEvery { recommendRepository.deleteRecommendUp(postId) } returns Result.success(Unit)

        // when
        viewModel.getPostDetail(postId, notificationId)
        viewModel.updateRecommendationUi(
            postId = postId,
            isChecked = false,
            isUpRecommendation = true,
        )

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
        val post = createPost(
            recommendation = Recommendation(
                upCount = Count(12),
                downCount = Count(12),
                isUp = false,
                isDown = true,
            ),
        )
        coEvery {
            postRepository.getPostDetail(
                postId,
                notificationId,
            )
        } returns Result.success(post)
        coEvery { recommendRepository.saveRecommendUp(postId) } returns Result.success(Unit)

        // when
        viewModel.getPostDetail(postId, notificationId)
        viewModel.updateRecommendationUi(
            postId = postId,
            isChecked = true,
            isUpRecommendation = true,
        )
        viewModel.updateRecommendationUi(
            postId = postId,
            isChecked = false,
            isUpRecommendation = false,
        )

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
        val post = createPost(
            recommendation = Recommendation(
                upCount = Count(12),
                downCount = Count(12),
                isUp = false,
                isDown = true,
            ),
        )
        coEvery {
            postRepository.getPostDetail(
                postId,
                notificationId,
            )
        } returns Result.success(post)
        coEvery { recommendRepository.deleteRecommendDown(postId) } returns Result.success(Unit)

        // when
        viewModel.getPostDetail(postId, notificationId)
        viewModel.updateRecommendationUi(
            postId = postId,
            isChecked = false,
            isUpRecommendation = false,
        )

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
        val post = createPost(
            recommendation = Recommendation(
                upCount = Count(12),
                downCount = Count(12),
                isUp = true,
                isDown = false,
            ),
        )
        coEvery {
            postRepository.getPostDetail(
                postId,
                notificationId,
            )
        } returns Result.success(post)
        coEvery { recommendRepository.saveRecommendDown(postId) } returns Result.success(Unit)

        // when
        viewModel.getPostDetail(postId, notificationId)
        viewModel.updateRecommendationUi(
            postId = postId,
            isChecked = false,
            isUpRecommendation = true,
        )
        viewModel.updateRecommendationUi(
            postId = postId,
            isChecked = true,
            isUpRecommendation = false,
        )

        // then
        val actual = Recommendation(
            upCount = Count(11),
            downCount = Count(13),
            isUp = false,
            isDown = true,
        ).toUiModel()
        assertEquals(actual, viewModel.recommendation.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `추천을 한 뒤, 응답이 오기 전까지 추천 기능이 비활성화된다`() = runTest {
        // given
        val post = createPost(
            recommendation = Recommendation(
                upCount = Count(12),
                downCount = Count(12),
                isUp = false,
                isDown = false,
            ),
        )
        coEvery {
            postRepository.getPostDetail(
                postId,
                notificationId,
            )
        } returns Result.success(post)
        coEvery { recommendRepository.saveRecommendUp(postId) } coAnswers {
            delay(3000L)
            Result.success(Unit)
        }

        // when & then
        viewModel.getPostDetail(postId, notificationId)

        // isRecommendationRequestDone의 기본값은 true이다.
        assertEquals(true, viewModel.isRecommendationRequestDone.value)

        // 통신을 요청하면 해당 값은 false로 바뀌고
        viewModel.updateRecommendationUi(
            postId = postId,
            isChecked = true,
            isUpRecommendation = true,
        )
        assertEquals(false, viewModel.isRecommendationRequestDone.value)

        // 응답을 받으면 다시 true로 바뀐다.
        advanceTimeBy(3001L)
        assertEquals(true, viewModel.isRecommendationRequestDone.value)
    }
}
