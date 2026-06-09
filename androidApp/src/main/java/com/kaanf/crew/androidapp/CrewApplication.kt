
package com.kaanf.crew.androidapp

import android.app.Application
import com.kaanf.crew.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class CrewApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@CrewApplication)
            androidLogger()
        }
    }
}
