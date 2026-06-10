package com.kaanf.crew.di

import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.crew.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule =
    module {
        single { SnackbarController() }
        viewModelOf(::MainViewModel)
    }
