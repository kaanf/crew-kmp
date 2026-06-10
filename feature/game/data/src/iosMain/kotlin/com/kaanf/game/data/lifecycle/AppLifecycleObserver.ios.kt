package com.kaanf.game.data.lifecycle

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationDidEnterBackgroundNotification
import platform.UIKit.UIApplicationState
import platform.UIKit.UIApplicationWillEnterForegroundNotification

actual class AppLifecycleObserver {
    actual val isInForeground: Flow<Boolean> = callbackFlow {
        val currentState = UIApplication.sharedApplication.applicationState
        val isCurrentlyInForeground = when(currentState) {
            UIApplicationState.UIApplicationStateActive -> true
            UIApplicationState.UIApplicationStateInactive -> true
            else -> false
        }
        send(isCurrentlyInForeground)

        val notificationCenter = NSNotificationCenter.defaultCenter

        val willEnterForegroundObserver = notificationCenter.addObserverForName(
            name = UIApplicationWillEnterForegroundNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) {
            trySend(true)
        }

        val backgroundObserver = notificationCenter.addObserverForName(
            name = UIApplicationDidEnterBackgroundNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) {
            trySend(false)
        }

        awaitClose {
            notificationCenter.removeObserver(willEnterForegroundObserver)
            notificationCenter.removeObserver(backgroundObserver)
        }
    }
}
