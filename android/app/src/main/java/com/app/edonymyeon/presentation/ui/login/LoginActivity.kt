package com.app.edonymyeon.presentation.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import app.edonymyeon.R
import app.edonymyeon.databinding.ActivityLoginBinding
import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource
import com.app.edonymyeon.data.datasource.auth.AuthRemoteDataSource
import com.app.edonymyeon.data.repository.AuthRepositoryImpl
import com.app.edonymyeon.presentation.util.makeSnackbar

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val viewModel: LoginViewModel by lazy {
        LoginViewModelFactory(
            AuthRepositoryImpl(
                AuthRemoteDataSource(),
                AuthLocalDataSource.getInstance(sharedPreferences),
            ),
        ).create(
            LoginViewModel::class.java,
        )
    }

    private val masterKey = MasterKey.Builder(this)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences(
        this,
        AuthLocalDataSource.AUTH_INFO,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initBinding()
        setObserver()
        setJoinClickListener()
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun setObserver() {
        viewModel.isLoginEnabled.observe(this) { isEnable ->
            if (!isEnable) {
                binding.svLogin.makeSnackbar(getString(R.string.login_check_logininfo_input))
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
                binding.svLogin.makeSnackbar(it)
            }
        }
    }

    private fun setJoinClickListener() {
        binding.btJoin.setOnClickListener {
            navigateToJoin()
            finish()
        }
    }

    private fun navigateToMain() {
    }

    private fun navigateToJoin() {
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}
