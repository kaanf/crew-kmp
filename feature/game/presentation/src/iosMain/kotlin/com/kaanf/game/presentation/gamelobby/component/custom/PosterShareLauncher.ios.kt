package com.kaanf.game.presentation.gamelobby.component.custom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.popoverPresentationController

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
internal actual fun rememberPosterShareLauncher(): suspend (ByteArray) -> Unit = remember {
    { bytes ->
        require(bytes.isNotEmpty())
        val data = bytes.usePinned { NSData.create(bytes = it.addressOf(0), length = bytes.size.toULong()) }
        val image = checkNotNull(UIImage.imageWithData(data))
        var presenter = checkNotNull(UIApplication.sharedApplication.keyWindow?.rootViewController)
        while (presenter.presentedViewController != null) presenter = presenter.presentedViewController!!
        val controller = UIActivityViewController(activityItems = listOf(image), applicationActivities = null)
        controller.popoverPresentationController?.apply {
            sourceView = presenter.view
            sourceRect = presenter.view.bounds
        }
        presenter.presentViewController(controller, animated = true, completion = null)
    }
}
