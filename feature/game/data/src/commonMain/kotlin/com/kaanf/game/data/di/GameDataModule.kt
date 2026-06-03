package com.kaanf.game.data.di

import com.kaanf.game.data.repository.GameSocketRepositoryImpl
import com.kaanf.game.data.repository.MatchRepositoryImpl
import com.kaanf.game.domain.repository.GameSocketRepository
import com.kaanf.game.domain.repository.MatchRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val gameDataModule =
    module {
        singleOf(::GameSocketRepositoryImpl) bind GameSocketRepository::class
        singleOf(::MatchRepositoryImpl) bind MatchRepository::class
    }
