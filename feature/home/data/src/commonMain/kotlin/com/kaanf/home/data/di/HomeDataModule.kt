package com.kaanf.home.data.di

import com.kaanf.home.data.repository.EventRepositoryImpl
import com.kaanf.home.domain.repository.EventRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val homeDataModule =
    module {
        singleOf(::EventRepositoryImpl) bind EventRepository::class
    }
