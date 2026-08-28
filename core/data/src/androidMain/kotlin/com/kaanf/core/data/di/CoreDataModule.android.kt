package com.kaanf.core.data.di

import android.content.pm.ApplicationInfo
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.kaanf.core.data.BuildEnvironment
import com.kaanf.core.data.device.AndroidDeviceIdProvider
import com.kaanf.core.data.networking.ConnectivityObserver
import com.kaanf.core.data.storage.createDataStore
import com.kaanf.core.domain.provider.DeviceIdProvider
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformCoreDataModule =
    module {
        single<DeviceIdProvider> { AndroidDeviceIdProvider(androidContext()) }
        single {
            val flags = androidContext().applicationInfo.flags
            BuildEnvironment(isDebug = flags and ApplicationInfo.FLAG_DEBUGGABLE != 0)
        }
        single { ConnectivityObserver(androidContext()) }
        single<HttpClientEngine> { OkHttp.create() }
        single<DataStore<Preferences>> {
            createDataStore(androidContext())
        }
    }
