package com.kaanf.game.data.di

import com.kaanf.game.data.repository.EventConnectionClientImpl
import com.kaanf.game.data.repository.MatchRepositoryImpl
import com.kaanf.game.domain.event.EventConnectionClient
import com.kaanf.game.domain.repository.MatchRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val gameDataModule =
    module {
        includes(platformGameDataModule)
        singleOf(::EventConnectionClientImpl) bind EventConnectionClient::class
        singleOf(::MatchRepositoryImpl) bind MatchRepository::class
    }
