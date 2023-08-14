package com.app.edonymyeon.presentation.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import app.edonymyeon.databinding.ActivityLoginBinding
import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource
import com.app.edonymyeon.data.datasource.auth.AuthRemoteDataSource
import com.app.edonymyeon.data.repository.AuthRepositoryImpl
import com.app.edonymyeon.data.service.client.RetrofitClient
import com.app.edonymyeon.presentation.ui.signup.SignUpActivity
import com.app.edonymyeon.presentation.util.makeSnackbar
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

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
        initKakaoLogin()
        setObserver()
        setKakaoClickListener()
        setJoinClickListener()
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initKakaoLogin() {
        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { _, error ->
                if (error == null) {
                    // 성공
                    Log.i(TAG, "토큰이 존재하여 로그인 성공")
                }
            }
        }
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
            AuthLocalDataSource.getInstance(sharedPreferences).getAuthToken(),
        )
    }

    private val kakaoLoginCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "로그인 실패", error)
        } else if (token != null) {
            Log.i(TAG, "로그인 성공 ${token.accessToken}")
            // 성공 이벤트
        }
    }

    private fun setKakaoClickListener() {
        binding.btLoginKakao.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null) {
                        Log.e(TAG, "카카오톡으로 로그인 실패", error)

                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }

                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(
                            this,
                            callback = kakaoLoginCallback,
                        )
                    } else if (token != null) {
                        Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                        // 성공 이벤트
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = kakaoLoginCallback)
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
        private const val TAG = "LoginActivity"

        fun newIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}
