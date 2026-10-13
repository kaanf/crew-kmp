package com.kaanf.crew.androidapp

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kaanf.crew.R
import org.koin.android.ext.android.inject

const val DEFAULT_NOTIFICATION_CHANNEL_ID = "default"
const val PUSH_LINK_KEY = "link"

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val pushTokenSync: PushTokenSync by inject()

    override fun onNewToken(token: String) {
        pushTokenSync.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val notification = message.notification ?: return
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        // Arka plandaki FCM davranışıyla aynı: data, açılan intent'in extras'ına gider.
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            message.data.forEach { (key, value) -> putExtra(key, value) }
        }
        val contentIntent = PendingIntent.getActivity(
            this,
            message.messageId.hashCode(),
            launchIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val built = NotificationCompat.Builder(this, DEFAULT_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()
        NotificationManagerCompat.from(this).notify(message.messageId.hashCode(), built)
    }
}
