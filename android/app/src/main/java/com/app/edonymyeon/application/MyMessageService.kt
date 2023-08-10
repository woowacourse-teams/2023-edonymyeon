package com.app.edonymyeon.application

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyMessageService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("testToken", "token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("testMessage", "messageId: ${message.messageId}")
        Log.d("testMessage", "messageType: ${message.notification?.body}")
        Log.d("testMessage", "messageType: ${message.notification?.title}")
    }
}
