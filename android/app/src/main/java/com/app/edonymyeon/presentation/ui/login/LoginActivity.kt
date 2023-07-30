package com.app.edonymyeon.presentation.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.edonymyeon.R
import app.edonymyeon.databinding.ActivityLoginBinding
import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource
import com.app.edonymyeon.data.datasource.user.UserRemoteDataSource
import com.app.edonymyeon.data.repository.UserRepositoryImpl
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val viewModel: LoginViewModel by lazy {
        LoginViewModelFactory(
            UserRepositoryImpl(
                UserRemoteDataSource(),
                AuthLocalDataSource.getInstance(this),
            ),
        ).create(
            LoginViewModel::class.java,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initBinding()

        viewModel.isLoginEnabled.observe(this) { isEnable ->
            if (!isEnable) {
                Snackbar.make(binding.svLogin, getString(R.string.login_check_logininfo_input), Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.isSuccess.observe(this) {
            if (it) {
                navigateToMain()
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(
                    binding.svLogin,
                    it,
                    Snackbar.LENGTH_LONG,
                ).show()
            }
        }
        setJoinClickListener()
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun navigateToMain() {
    }

    private fun setJoinClickListener() {
        binding.btJoin.setOnClickListener {
            navigateToJoin()
            finish()
        }
    }

    private fun navigateToJoin() {
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}
