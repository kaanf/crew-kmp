package com.kaanf.home.presentation.eventdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.popoverPresentationController

@OptIn(ExperimentalForeignApi::class)
@Composable
internal actual fun rememberEventShareLauncher(): (String) -> Unit = remember {
    { text ->
        var presenter = checkNotNull(UIApplication.sharedApplication.keyWindow?.rootViewController)
        while (presenter.presentedViewController != null) {
            presenter = presenter.presentedViewController!!
        }
        val controller = UIActivityViewController(activityItems = listOf(text), applicationActivities = null)
        controller.popoverPresentationController?.apply {
            sourceView = presenter.view
            sourceRect = presenter.view.bounds
        }
        presenter.presentViewController(controller, animated = true, completion = null)
    }
}
