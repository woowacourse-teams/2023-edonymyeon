package com.app.edonymyeon.presentation.ui.signup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource
import com.app.edonymyeon.data.datasource.auth.AuthRemoteDataSource
import com.app.edonymyeon.data.repository.AuthRepositoryImpl
import com.app.edonymyeon.databinding.ActivitySignUpBinding
import com.app.edonymyeon.presentation.ui.login.LoginActivity

class SignUpActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    private val viewModel: SignUpViewModel by viewModels {
        SignUpViewModelFactory(
            AuthRepositoryImpl(
                AuthLocalDataSource.getInstance(
                    getSharedPreferences(
                        AuthLocalDataSource.AUTH_INFO,
                        MODE_PRIVATE,
                    ),
                ),
                AuthRemoteDataSource(),
            ),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initBinding()
        initListener()
        initObserver()
    }

    private fun initObserver() {
        viewModel.isSignUpSuccess.observe(this) {
            if (it) {
                startActivity(LoginActivity.newIntent(this))
                finish()
            }
        }
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initListener() {
        binding.btJoin.setOnClickListener {
            viewModel.signUp(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString(),
                binding.etNickname.text.toString(),
            )
        }
        binding.etEmail.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                viewModel.verifyEmail(binding.etEmail.text.toString())
            }
        }

        binding.etNickname.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                viewModel.verifyNickname(binding.etNickname.text.toString())
            }
        }
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, SignUpActivity::class.java)
    }
}
