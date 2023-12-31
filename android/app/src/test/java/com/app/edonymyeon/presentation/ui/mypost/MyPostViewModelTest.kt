package com.app.edonymyeon.presentation.ui.mypost

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.app.edonymyeon.mapper.toUiModel
import com.domain.edonymyeon.model.Consumption
import com.domain.edonymyeon.model.Count
import com.domain.edonymyeon.model.MyPost
import com.domain.edonymyeon.model.MyPosts
import com.domain.edonymyeon.model.ReactionCount
import com.domain.edonymyeon.repository.AuthRepository
import com.domain.edonymyeon.repository.PostRepository
import com.domain.edonymyeon.repository.ProfileRepository
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MyPostViewModelTest {

    private lateinit var viewModel: MyPostViewModel
    private lateinit var profileRepository: ProfileRepository
    private lateinit var postRepository: PostRepository
    private lateinit var authRepository: AuthRepository
    private val notificationId = 0L

    private val fakeMyPost = List(3) {
        MyPost(
            id = (it + 1).toLong(),
            title = "가나다라",
            image = "",
            content = "아야하언ㅇㄹㄴㅇㄹ",
            createdAt = "2023-08-01T22:12:06.051634",
            consumption = Consumption(
                type = "NONE",
                purchasePrice = 0,
                year = 0,
                month = 0,
            ),
            reactionCount = ReactionCount(Count(0), Count(0)),
        )
    }

    private val fakeMyPosts = MyPosts(
        fakeMyPost,
        true,
    )

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        profileRepository = mockk()
        postRepository = mockk()
        authRepository = mockk()
        viewModel = MyPostViewModel(profileRepository, postRepository, authRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `내가 쓴 글을 불러온다`() {
        // given
        val page = 0
        coEvery { profileRepository.getMyPosts(page, notificationId) } returns Result.success(
            fakeMyPosts,
        )

        // when
        viewModel.getMyPosts(notificationId)

        // then
        assertEquals(fakeMyPost.map { it.toUiModel() }, viewModel.posts.value)
    }

    @Test
    fun `구매 확정을 한다`() {
        // given
        val postId = 1L
        val consumption = Consumption(
            type = "PURCHASE",
            purchasePrice = 10000,
            year = 2023,
            month = 7,
        )

        coEvery { profileRepository.getMyPosts(0, notificationId) } returns Result.success(
            fakeMyPosts,
        )
        coEvery {
            profileRepository.postPurchaseConfirm(
                postId,
                consumption.purchasePrice,
                consumption.year,
                consumption.month,
            )
        } returns Result.success(Unit)

        // when
        viewModel.getMyPosts(notificationId)
        viewModel.postPurchaseConfirm(
            postId,
            consumption.purchasePrice,
            consumption.year,
            consumption.month,
        )

        // then
        assertEquals(
            viewModel.posts.value?.find { it.id == postId }?.consumption,
            consumption.toUiModel(),
        )
    }

    @Test
    fun `절약 확정을 한다`() {
        // given
        val postId = 1L
        val consumption = Consumption(
            type = "SAVING",
            purchasePrice = 0,
            year = 2023,
            month = 7,
        )

        coEvery { profileRepository.getMyPosts(0, notificationId) } returns Result.success(
            fakeMyPosts,
        )
        coEvery {
            profileRepository.postSavingConfirm(
                postId,
                consumption.year,
                consumption.month,
            )
        } returns Result.success(Unit)

        // when
        viewModel.getMyPosts(notificationId)
        viewModel.postSavingConfirm(
            postId,
            consumption.year,
            consumption.month,
        )

        // then
        assertEquals(
            viewModel.posts.value?.find { it.id == postId }?.consumption,
            consumption.toUiModel(),
        )
    }

    @Test
    fun `확정 취소를 한다`() {
        // given
        val postId = 1L
        val consumption = Consumption(
            type = "NONE",
            purchasePrice = 0,
            year = 0,
            month = 0,
        )

        coEvery { profileRepository.getMyPosts(0, notificationId) } returns Result.success(
            fakeMyPosts,
        )
        coEvery {
            profileRepository.deleteConfirm(
                postId,
            )
        } returns Result.success(Unit)

        // when
        viewModel.getMyPosts(notificationId)
        viewModel.deleteConfirm(
            postId,
        )

        // then
        assertEquals(
            viewModel.posts.value?.find { it.id == postId }?.consumption,
            consumption.toUiModel(),
        )
    }

    @Test
    fun `구입 가격을 설정한다`() {
        // given
        val price = "2000"

        // when
        viewModel.setPurchasePrice(price)

        // then
        assertEquals(viewModel.price.value, price)
    }
}
