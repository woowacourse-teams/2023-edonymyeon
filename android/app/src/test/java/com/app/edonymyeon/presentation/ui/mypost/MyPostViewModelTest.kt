package com.app.edonymyeon.presentation.ui.mypost

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.app.edonymyeon.mapper.toUiModel
import com.domain.edonymyeon.model.Consumption
import com.domain.edonymyeon.model.MyPost
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

    private val fakeMyPosts = listOf(
        MyPost(
            id = 1L,
            title = "가나다라",
            image = "",
            content = "아야하언ㅇㄹㄴㅇㄹ",
            createdAt = "1일전",
            consumption = Consumption(
                type = "NONE",
                purchasePrice = 0,
                year = 0,
                month = 0,
            ),
        ),
        MyPost(
            id = 2L,
            title = "가나다라",
            image = "",
            content = "아야하언ㅇㄹㄴㅇㄹ",
            createdAt = "1일전",
            consumption = Consumption(
                type = "PURCHASE",
                purchasePrice = 1000,
                year = 2023,
                month = 7,
            ),
        ),
        MyPost(
            id = 3L,
            title = "가나다라",
            image = "",
            content = "아야하언ㅇㄹㄴㅇㄹ",
            createdAt = "1일전",
            consumption = Consumption(
                type = "SAVING",
                purchasePrice = 0,
                year = 2023,
                month = 4,
            ),
        ),
    )

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        profileRepository = mockk()
        viewModel = MyPostViewModel(profileRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `내가 쓴 글을 불러온다`() {
        // given
        val size = 20
        val page = 0
        coEvery { profileRepository.getMyPosts(size, page) } returns Result.success(fakeMyPosts)

        // when
        viewModel.getMyPosts(size, page)

        // then
        assertEquals(fakeMyPosts.map { it.toUiModel() }, viewModel.posts.value)
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

        coEvery { profileRepository.getMyPosts(20, 0) } returns Result.success(fakeMyPosts)
        coEvery {
            profileRepository.postPurchaseConfirm(
                postId,
                consumption.purchasePrice,
                consumption.year,
                consumption.month,
            )
        } returns Result.success(Unit)

        // when
        viewModel.getMyPosts(20, 0)
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

        coEvery { profileRepository.getMyPosts(20, 0) } returns Result.success(fakeMyPosts)
        coEvery {
            profileRepository.postSavingConfirm(
                postId,
                consumption.year,
                consumption.month,
            )
        } returns Result.success(Unit)

        // when
        viewModel.getMyPosts(20, 0)
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

        coEvery { profileRepository.getMyPosts(20, 0) } returns Result.success(fakeMyPosts)
        coEvery {
            profileRepository.deleteConfirm(
                postId,
            )
        } returns Result.success(Unit)

        // when
        viewModel.getMyPosts(20, 0)
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
    fun `넘버 피커에 들어갈 년도와 달을 받는다`() {
        // given
        val postId = 1L
        val yearMonth = hashMapOf<Int, List<Int>>()
        yearMonth[2023] = listOf(8)
        coEvery { profileRepository.getMyPosts(20, 0) } returns Result.success(fakeMyPosts)

        // when
        viewModel.getYearMonth(postId)

        // then
        assertEquals(viewModel.yearMonth.value, yearMonth)
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
