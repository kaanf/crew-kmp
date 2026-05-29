package com.kaanf.home.presentation.di

import com.kaanf.home.domain.usecase.GetEventsUseCase
import com.kaanf.home.presentation.dashboard.DashboardViewModel
import com.kaanf.home.presentation.eventdetail.EventDetailViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homePresentationModule =
    module {
        factoryOf(::GetEventsUseCase)
        viewModelOf(::DashboardViewModel)
        viewModelOf(::EventDetailViewModel)
    }
