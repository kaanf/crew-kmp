package com.kaanf.game.data.di

import com.kaanf.game.data.lifecycle.AppLifecycleObserver
import com.kaanf.game.data.network.ConnectionErrorHandler
import com.kaanf.game.data.network.ConnectivityObserver
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformGameDataModule =
    module {
        single { ConnectivityObserver(androidContext()) }
        single { AppLifecycleObserver() }
        single { ConnectionErrorHandler() }
    }
