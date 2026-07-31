package com.kaanf.game.data.di

import com.kaanf.game.data.lifecycle.AppLifecycleObserver
import com.kaanf.game.data.network.ConnectionErrorHandler
import org.koin.dsl.module

actual val platformGameDataModule =
    module {
        single { AppLifecycleObserver() }
        single { ConnectionErrorHandler() }
    }
