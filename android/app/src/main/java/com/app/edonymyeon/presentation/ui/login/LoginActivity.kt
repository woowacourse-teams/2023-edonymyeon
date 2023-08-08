package com.app.edonymyeon.presentation.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource
import com.app.edonymyeon.data.datasource.auth.AuthRemoteDataSource
import com.app.edonymyeon.data.repository.AuthRepositoryImpl
import com.app.edonymyeon.data.service.client.RetrofitClient
import com.app.edonymyeon.databinding.ActivityLoginBinding
import com.app.edonymyeon.presentation.ui.main.MainActivity
import com.app.edonymyeon.presentation.ui.signup.SignUpActivity
import com.app.edonymyeon.presentation.util.makeSnackbar

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val viewModel: LoginViewModel by lazy {
        LoginViewModelFactory(
            AuthRepositoryImpl(
                AuthLocalDataSource.getInstance(sharedPreferences),
                AuthRemoteDataSource(),
            ),
        ).create(
            LoginViewModel::class.java,
        )
    }

    private val masterKey by lazy {
        MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val sharedPreferences by lazy {
        EncryptedSharedPreferences(
            this,
            AuthLocalDataSource.AUTH_INFO,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

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
        viewModel.isSuccess.observe(this) {
            if (it) {
                setRetrofitToken()
                navigateToMain()
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
            AuthLocalDataSource.getInstance(sharedPreferences).getAuthToken() ?: "",
        )
    }

    private fun navigateToMain() {
        startActivity(MainActivity.newIntent(this))
    }

    private fun navigateToJoin() {
        startActivity(SignUpActivity.newIntent(this))
    }

    private fun setJoinClickListener() {
        binding.btJoin.setOnClickListener {
            navigateToJoin()
            finish()
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}
