package com.app.edonymyeon.data.service.fcm

import com.google.firebase.messaging.FirebaseMessaging

object FCMToken {
    fun getFCMToken(token: (String?) -> Unit) {
        runCatching {
            FirebaseMessaging.getInstance().token.apply {
                addOnCompleteListener {
                    if (!it.isSuccessful) {
                        token(null)
                    }
                    token(it.result)
                }
                addOnCanceledListener {
                    token(null)
                }
                addOnFailureListener {
                    token(null)
                }
            }
        }
    }
}
