package com.kaanf.home.presentation.eventdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import coil3.PlatformContext
import coil3.toBitmap
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.skia.EncodedImageFormat
import platform.Foundation.NSData
import platform.Foundation.NSItemProvider
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.LinkPresentation.LPLinkMetadata
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIActivityItemSourceProtocol
import platform.UIKit.UIApplication
import platform.UIKit.popoverPresentationController
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
internal actual fun rememberEventShareLauncher(): (EventShareContent) -> Unit {
    val scope = rememberCoroutineScope()
    val launching = remember { mutableStateOf(false) }
    return remember(scope) {
        { content ->
            if (!launching.value) {
                launching.value = true
                scope.launch {
                    try {
                        val preview = eventSharePreviewImage(content)
                        var presenter = UIApplication.sharedApplication.keyWindow?.rootViewController
                        while (presenter?.presentedViewController != null) {
                            presenter = presenter.presentedViewController
                        }
                        if (presenter != null) {
                            val item = EventShareItemSource(content, preview)
                            val controller = UIActivityViewController(activityItems = listOf(item), applicationActivities = null)
                            controller.popoverPresentationController?.apply {
                                sourceView = presenter.view
                                sourceRect = presenter.view.bounds
                            }
                            presenter.presentViewController(controller, animated = true, completion = null)
                        }
                    } finally {
                        launching.value = false
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private suspend fun eventSharePreviewImage(content: EventShareContent): NSData? = try {
    val image = loadEventShareImage(PlatformContext.INSTANCE, content.imageUrl)
    if (image == null) null else withContext(Dispatchers.Default) {
        val bitmap = org.jetbrains.skia.Image.makeFromBitmap(image.toBitmap())
        try {
            val encoded = bitmap.encodeToData(EncodedImageFormat.JPEG, 85)
                ?: return@withContext null
            try {
                val bytes = encoded.bytes
                bytes.usePinned { NSData.create(bytes = it.addressOf(0), length = bytes.size.toULong()) }
            } finally {
                encoded.close()
            }
        } finally {
            bitmap.close()
        }
    }
} catch (exception: CancellationException) {
    throw exception
} catch (_: Exception) {
    null
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private class EventShareItemSource(
    private val content: EventShareContent,
    preview: NSData?,
) : NSObject(), UIActivityItemSourceProtocol {
    private val metadata = LPLinkMetadata().apply {
        title = content.title
        originalURL = NSURL.URLWithString(content.url)
        URL = originalURL
        if (preview != null) imageProvider = NSItemProvider(item = preview, typeIdentifier = "public.jpeg")
    }

    override fun activityViewControllerPlaceholderItem(activityViewController: UIActivityViewController): Any = content.text

    override fun activityViewController(
        activityViewController: UIActivityViewController,
        itemForActivityType: String?,
    ): Any = content.text

    // UIKit declares this LinkPresentation type through an Objective-C forward declaration.
    override fun activityViewControllerLinkMetadata(
        activityViewController: UIActivityViewController,
    ): objcnames.classes.LPLinkMetadata? = metadata as objcnames.classes.LPLinkMetadata
}
