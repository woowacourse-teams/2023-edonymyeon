package com.app.edonymyeon.presentation.common.fragment

import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import app.edonymyeon.R
import com.app.edonymyeon.data.common.FetchState
import com.app.edonymyeon.presentation.common.viewmodel.BaseViewModel
import com.app.edonymyeon.presentation.ui.login.LoginActivity
import com.app.edonymyeon.presentation.util.makeSnackbar
import com.app.edonymyeon.presentation.util.makeSnackbarWithEvent

abstract class BaseFragment<B : ViewDataBinding, VM : BaseViewModel>(private val inflate: (LayoutInflater) -> B) :
    Fragment() {
    lateinit var binding: B

    abstract val viewModel: VM
    abstract val inflater: LayoutInflater
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(inflater)
        viewModel.fetchState.observe(this) {
            when (it) {
                is FetchState.ParseError, FetchState.WrongConnection, FetchState.BadInternet -> {
                    binding.root.makeSnackbar("네트워크 연결 상태를 확인해주세요.")
                }

                is FetchState.NoAuthorization -> {
                    viewModel.clearToken1()
                    binding.root.makeSnackbarWithEvent(
                        message = it.customThrowable.message,
                        eventTitle = getString(R.string.login_title),
                    ) { navigateToLogin() }
                }

                is FetchState.Fail -> {
                    binding.root.makeSnackbar(it.customThrowable.message)
                }
            }
        }
    }

    private fun navigateToLogin() {
        startActivity(LoginActivity.newIntent(this.requireContext()))
    }
}
