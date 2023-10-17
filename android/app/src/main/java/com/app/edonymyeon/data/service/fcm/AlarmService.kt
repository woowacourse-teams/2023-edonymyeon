package com.app.edonymyeon.data.service.fcm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.edonymyeon.R
import com.app.edonymyeon.presentation.ui.main.MainActivity
import com.app.edonymyeon.presentation.ui.mypost.MyPostActivity
import com.app.edonymyeon.presentation.ui.postdetail.PostDetailActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    // background는 MainActivity, forground는 아래 로직을 탐
    @SuppressLint("MissingPermission")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        createNotificationChannel()
        CoroutineScope(Dispatchers.Main).launch {
            alarmOn.value = true
        }

        val intent = when (message.data["navigateTo"].toString()) {
            "POST" -> {
                PostDetailActivity.newIntent(
                    this,
                    message.data["postId"]?.toLong() ?: 0,
                    message.data["notificationId"]?.toLong() ?: 0,
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }

            "MYPOST" -> {
                MyPostActivity.newIntent(
                    this,
                    message.data["notificationId"]?.toLong() ?: 0,
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }

            else -> {
                MainActivity.newIntent(this).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
        }

        val builder =
            NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(R.mipmap.ic_edonymyeon_round)
                .setContentTitle(message.notification?.title)
                .setContentText(message.notification?.body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(
                    PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_MUTABLE,
                    ),
                ).setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify((message.data["id"] ?: "0").toInt(), builder.build())
        }
    }

    private fun createNotificationChannel() {
        val name = "이돈이면 채널" // getString()
        val descriptionText = "이돈이면 채널입니다" // getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "Edonymyeon"
        private val alarmOn: MutableLiveData<Boolean> = MutableLiveData()
        val isAlarmOn: LiveData<Boolean>
            get() = alarmOn
    }
}
