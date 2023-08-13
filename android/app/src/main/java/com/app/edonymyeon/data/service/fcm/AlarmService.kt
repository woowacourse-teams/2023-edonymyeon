package com.app.edonymyeon.data.service.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AlarmService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("testToken", "token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // TODO("알림 설정 마이페이지에서 추가")
        // TODO("알림 권한") 글을 등록했을 때 물어보기, 권한 설정시 물어보기, 33버전 이후로 무조건 받아야 사용가능, 12이하는 안 물어봐도 사용가능하다.
        // TODO("알림이 남아있는지 여부확인 빨간색 표시 로그인 시에 알림 조회, 로그아웃시 초기화")
//      앱이 켜져있는 상태인지, 꺼진 상태인지 체크한다.
    }
}
