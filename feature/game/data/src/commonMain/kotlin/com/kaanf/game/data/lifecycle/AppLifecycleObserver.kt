package com.kaanf.game.data.lifecycle

import kotlinx.coroutines.flow.Flow

expect class AppLifecycleObserver {
    val isInForeground: Flow<Boolean>
}
