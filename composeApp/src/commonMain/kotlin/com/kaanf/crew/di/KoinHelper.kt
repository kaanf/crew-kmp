package com.kaanf.crew.di

import com.kaanf.auth.data.di.authDataModule
import com.kaanf.auth.presentation.di.authPresentationModule
import com.kaanf.core.data.di.coreDataModule
import com.kaanf.home.data.di.homeDataModule
import com.kaanf.home.presentation.di.homePresentationModule
import org.koin.core.module.Module
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    initKoin(
        config = config,
        additionalModules = emptyList(),
    )
}

fun initKoin(
    config: KoinAppDeclaration? = null,
    additionalModules: List<Module>,
) {
    startKoin {
        config?.invoke(this)
        modules(
            coreDataModule,
            authDataModule,
            authPresentationModule,
            homeDataModule,
            homePresentationModule,
            appModule,
            *additionalModules.toTypedArray(),
        )
    }
}
