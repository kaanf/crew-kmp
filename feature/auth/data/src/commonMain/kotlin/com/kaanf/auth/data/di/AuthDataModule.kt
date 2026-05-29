package com.kaanf.auth.data.di

import com.kaanf.auth.data.repository.AuthRepositoryImpl
import com.kaanf.auth.domain.repository.AuthRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authDataModule =
    module {
        singleOf(::AuthRepositoryImpl) bind AuthRepository::class
    }
