package com.app.edonymyeon.presentation.ui.main.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.app.edonymyeon.mapper.toAllPostItemUiModel
import com.domain.edonymyeon.model.Count
import com.domain.edonymyeon.model.PostItem
import com.domain.edonymyeon.model.PostItems
import com.domain.edonymyeon.model.ReactionCount
import com.domain.edonymyeon.repository.PostRepository
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
import java.time.LocalDate

class HomeViewModelTest {
    private lateinit var viewModel: HomeViewModel
    private lateinit var postRepository: PostRepository

    private val fakeAllPost = List(5) {
        PostItem(
            id = it.toLong(),
            title = "$it title",
            image = null,
            content = "$it content",
            createdAt = LocalDate.parse("2023-03-08").toString(),
            nickname = "$it nickname",
            reactionCount = ReactionCount(
                viewCount = Count(it),
                commentCount = Count(it),
            ),
        )
    }

    private val fakeAllPosts = PostItems(
        fakeAllPost,
        true,
    )

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        postRepository = mockk()
        viewModel = HomeViewModel(postRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `홈 화면에 보이는 전체 게시글을 불러온다`() {
        // given
        val size = 5
        val page = 0
        coEvery { postRepository.getPosts(size, page) } returns Result.success(fakeAllPosts)

        // when
        viewModel.getAllPosts()

        // then
        assertEquals(fakeAllPost.map { it.toAllPostItemUiModel() }, viewModel.allPosts.value)
    }
}
