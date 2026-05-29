package com.kaanf.core.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.kaanf.core.data.device.AndroidDeviceIdProvider
import com.kaanf.core.data.storage.createDataStore
import com.kaanf.core.domain.provider.DeviceIdProvider
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformCoreDataModule =
    module {
        single<DeviceIdProvider> { AndroidDeviceIdProvider(androidContext()) }
        single<HttpClientEngine> { OkHttp.create() }
        single<DataStore<Preferences>> {
            createDataStore(androidContext())
        }
    }
