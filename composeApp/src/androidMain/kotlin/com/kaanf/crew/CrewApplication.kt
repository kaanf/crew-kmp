package com.kaanf.crew

import android.app.Application
import com.kaanf.crew.di.initKoin
import dev.skymansandy.wiretap.helper.launcher.enableWiretapLauncher
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class CrewApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@CrewApplication)
            androidLogger()
        }

        // Wiretap ağ inceleyicisini cihazı sallayarak açılacak şekilde etkinleştirir.
        // Release'te wiretap-launcher-noop ile no-op olur.
        enableWiretapLauncher()
    }
}
