package com.app.edonymyeon.presentation.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.app.edonymyeon.data.common.CustomThrowable
import com.domain.edonymyeon.repository.AuthRepository
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

class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private lateinit var authRepository: AuthRepository

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        authRepository = mockk()
        viewModel = LoginViewModel(authRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `이메일과 패스워드를 둘 다 입력하지 않으면 에러메시지가 발생한다`() {
        // given
        val email = ""
        val password = ""
        val errorMessage = "이메일과 패스워드는 필수 입력항목입니다."

        // when
        viewModel.onLoginClick(email, password)

        // then
        assertEquals(viewModel.errorMessage.value, errorMessage)
    }

    @Test
    fun `이메일과 패스워드를 둘 중 하나를 입력하지 않으면 에러메시지가 발생한다`() {
        // given
        val email = ""
        val password = "seho123"
        val errorMessage = "이메일과 패스워드는 필수 입력항목입니다."

        // when
        viewModel.onLoginClick(email, password)

        // then
        assertEquals(viewModel.errorMessage.value, errorMessage)
    }

    @Test
    fun `이메일과 비밀번호를 입력하고 로그인에 성공한다`() {
        // given
        val email = "sehozzang@nate.com"
        val password = "seho123"
        val deviceToken = "deviceToken"
        coEvery { authRepository.login(email, password, deviceToken) } returns Result.success(Unit)

        // when
        viewModel.onLoginClick(email, password)

        // then
        assertEquals(viewModel.isSuccess.value, true)
    }

    @Test
    fun `이메일과 비밀번호를 입력하고 로그인에 실패하면 에러메시지가 발생한다`() {
        // given
        val email = "sehozzang@nate.com"
        val password = "seho123"
        val deviceToken = "deviceToken"
        val errorMessage = "로그인에 실패했습니다"
        coEvery {
            authRepository.login(
                email,
                password,
                deviceToken,
            )
        } returns Result.failure(CustomThrowable(400, errorMessage))

        // when
        viewModel.onLoginClick(email, password)

        // then
        assertEquals(viewModel.isSuccess.value, false)
        assertEquals(viewModel.errorMessage.value, errorMessage)
    }
}
