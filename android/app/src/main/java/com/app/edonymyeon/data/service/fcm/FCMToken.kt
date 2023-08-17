package com.app.edonymyeon.data.service.fcm

import com.google.firebase.messaging.FirebaseMessaging

object FCMToken {
    fun getFCMToken(token: (String?) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                token(null)
            }
            token(it.result)
        }
    }
}
