package com.app.edonymyeon.presentation.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import app.edonymyeon.databinding.ActivityLoginBinding
import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource
import com.app.edonymyeon.data.datasource.auth.AuthRemoteDataSource
import com.app.edonymyeon.data.repository.AuthRepositoryImpl
import com.app.edonymyeon.data.service.client.RetrofitClient
import com.app.edonymyeon.presentation.common.activity.BaseActivity
import com.app.edonymyeon.presentation.ui.signup.SignUpActivity
import com.app.edonymyeon.presentation.util.loginByKakao
import com.app.edonymyeon.presentation.util.makeSnackbar

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(
    { ActivityLoginBinding.inflate(it) },
) {
    override val viewModel: LoginViewModel by lazy {
        LoginViewModelFactory(
            AuthRepositoryImpl(
                AuthLocalDataSource(),
                AuthRemoteDataSource(),
            ),
        ).create(
            LoginViewModel::class.java,
        )
    }
    override val inflater: LayoutInflater by lazy { LayoutInflater.from(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initBinding()
        setObserver()
        setKakaoClickListener()
        setJoinClickListener()
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun setObserver() {
        viewModel.isSuccess.observe(this) {
            if (it) {
                setRetrofitToken()
                finish()
            } else {
                setEmailAndPasswordEmpty()
            }
        }
        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                binding.svLogin.makeSnackbar(it)
            }
        }
    }

    private fun setEmailAndPasswordEmpty() {
        binding.etEmail.setText("")
        binding.etPassword.setText("")
    }

    private fun setRetrofitToken() {
        RetrofitClient.getInstance().updateAccessToken(
            AuthLocalDataSource().getAuthToken(),
        )
    }

    private fun setKakaoClickListener() {
        binding.btLoginKakao.setOnClickListener {
            loginByKakao(this) { accessToken ->
                viewModel.loginByKakao(accessToken)
            }
        }
    }

    private fun setJoinClickListener() {
        binding.btJoin.setOnClickListener {
            navigateToJoin()
            finish()
        }
    }

    private fun navigateToJoin() {
        startActivity(SignUpActivity.newIntent(this))
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}
