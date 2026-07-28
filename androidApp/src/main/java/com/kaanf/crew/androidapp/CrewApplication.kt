
package com.kaanf.crew.androidapp

import android.app.Application
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import com.kaanf.crew.R
import com.kaanf.crew.di.initKoin
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class CrewApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@CrewApplication)
            androidLogger()
            modules(module { singleOf(::PushTokenSync) })
        }

        createDefaultNotificationChannel()
        get<PushTokenSync>().start()

        // Debug-only: installs the Wiretap launcher. No-op in release (see release source set).
        installDevTools()
    }

    private fun createDefaultNotificationChannel() {
        val channel = NotificationChannelCompat.Builder(
            DEFAULT_NOTIFICATION_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_HIGH,
        ).setName(getString(R.string.notification_channel_default)).build()
        NotificationManagerCompat.from(this).createNotificationChannel(channel)
    }
}
