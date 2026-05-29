package com.kaanf.core.data.di

import com.kaanf.core.data.session.DataStoreSessionStorage
import com.kaanf.core.data.logging.KermitLogger
import com.kaanf.core.data.networking.HttpClientFactory
import com.kaanf.core.data.repository.UserRepositoryImpl
import com.kaanf.core.data.repository.UserStoreImpl
import com.kaanf.core.domain.repository.SessionStorage
import com.kaanf.core.domain.logging.CrewLogger
import com.kaanf.core.domain.repository.UserRepository
import com.kaanf.core.domain.repository.UserStore
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformCoreDataModule: Module

val coreDataModule =
    module {
        includes(platformCoreDataModule)
        single<CrewLogger> { KermitLogger }
        singleOf(::DataStoreSessionStorage) bind SessionStorage::class
        singleOf(::UserRepositoryImpl) bind UserRepository::class
        singleOf(::UserStoreImpl) bind UserStore::class
        single {
            HttpClientFactory(
                get(), get()
            ).create(get())
        }
    }
