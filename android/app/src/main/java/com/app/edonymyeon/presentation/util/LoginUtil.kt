package com.app.edonymyeon.presentation.util

import android.content.Context
import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

private const val TAG_KAKAO = "KakaoLogin"
private const val KAKAO_SUCCESS = "카카오 로그인 성공"
private const val KAKAO_FAILURE = "카카오 로그인 실패"

private fun getKakaoLoginCallback(loginSuccessEvent: () -> Unit): (OAuthToken?, Throwable?) -> Unit =
    { token, error ->
        if (error != null) {
            Log.e(TAG_KAKAO, "$KAKAO_FAILURE $error")
        } else if (token != null) {
            Log.i(TAG_KAKAO, "$KAKAO_SUCCESS ${token.accessToken}")
            loginSuccessEvent()
        }
    }

fun loginByKakao(context: Context, loginSuccessEvent: () -> Unit) {
    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                Log.e(TAG_KAKAO, "$KAKAO_FAILURE $error")

                // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우 (예: 뒤로 가기)
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    return@loginWithKakaoTalk
                }

                // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                UserApiClient.instance.loginWithKakaoAccount(
                    context,
                    callback = getKakaoLoginCallback(loginSuccessEvent),
                )
            } else if (token != null) {
                Log.i(TAG_KAKAO, "$KAKAO_SUCCESS ${token.accessToken}")
                loginSuccessEvent()
            }
        }
    } else {
        UserApiClient.instance.loginWithKakaoAccount(
            context,
            callback = getKakaoLoginCallback(loginSuccessEvent),
        )
    }
}
