package com.kaanf.core.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.kaanf.core.data.device.IosDeviceIdProvider
import com.kaanf.core.data.storage.createDataStore
import com.kaanf.core.domain.provider.DeviceIdProvider
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module

actual val platformCoreDataModule =
    module {
        single<DeviceIdProvider> { IosDeviceIdProvider() }
        single<HttpClientEngine> { Darwin.create() }
        single<DataStore<Preferences>> { createDataStore() }
    }
