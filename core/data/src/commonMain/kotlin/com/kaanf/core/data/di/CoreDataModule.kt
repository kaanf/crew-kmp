package com.kaanf.core.data.di

import com.kaanf.core.data.session.DataStoreSessionStorage
import com.kaanf.core.data.settings.DataStoreLanguageStore
import com.kaanf.core.data.logging.KermitLogger
import com.kaanf.core.data.networking.HttpClientFactory
import com.kaanf.core.data.networking.SessionRefresher
import com.kaanf.core.data.networking.clearBearerToken
import io.ktor.client.HttpClient
import com.kaanf.core.data.repository.AuthSessionRepositoryImpl
import com.kaanf.core.data.repository.UserRepositoryImpl
import com.kaanf.core.data.repository.UserStoreImpl
import com.kaanf.core.domain.repository.AuthSessionRepository
import com.kaanf.core.domain.repository.SessionStorage
import com.kaanf.core.domain.logging.CrewLogger
import com.kaanf.core.domain.repository.LanguageStore
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
        single<SessionStorage> {
            // clearBearerToken lambda'sı HttpClient'ı tembel çözer: SessionStorage kurulurken
            // client henüz yok, bu yüzden çağrı anına ertelenir (döngüsel bağımlılık olmaz).
            DataStoreSessionStorage(get()) { get<HttpClient>().clearBearerToken() }
        }
        singleOf(::DataStoreLanguageStore) bind LanguageStore::class
        singleOf(::AuthSessionRepositoryImpl) bind AuthSessionRepository::class
        singleOf(::UserRepositoryImpl) bind UserRepository::class
        singleOf(::UserStoreImpl) bind UserStore::class
        singleOf(::SessionRefresher)
        single {
            HttpClientFactory(
                get(), get(), get()
            ).create(get())
        }
    }
