package com.kaanf.crew.androidapp

import android.Manifest
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kaanf.crew.R
import org.koin.android.ext.android.inject

const val DEFAULT_NOTIFICATION_CHANNEL_ID = "default"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val pushTokenSync: PushTokenSync by inject()

    override fun onNewToken(token: String) {
        pushTokenSync.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        // App background'dayken notification mesajlarını sistem gösterir; burası foreground yolu.
        val notification = message.notification ?: return
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE,
        )
        val built = NotificationCompat.Builder(this, DEFAULT_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()
        NotificationManagerCompat.from(this).notify(message.messageId.hashCode(), built)
    }
}
