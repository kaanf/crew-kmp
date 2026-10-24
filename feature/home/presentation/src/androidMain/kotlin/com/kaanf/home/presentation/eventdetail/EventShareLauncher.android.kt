package com.kaanf.home.presentation.eventdetail

import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import coil3.toBitmap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
internal actual fun rememberEventShareLauncher(): (EventShareContent) -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launching = remember { mutableStateOf(false) }
    return remember(context, scope) {
        { content ->
            if (!launching.value) {
                launching.value = true
                scope.launch {
                    try {
                        val preview = eventSharePreviewUri(context, content)
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, content.text)
                            putExtra(Intent.EXTRA_TITLE, content.title)
                            if (preview != null) {
                                clipData = ClipData.newRawUri(content.title, preview)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                        }
                        context.startActivity(Intent.createChooser(intent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    } finally {
                        launching.value = false
                    }
                }
            }
        }
    }
}

private suspend fun eventSharePreviewUri(
    context: android.content.Context,
    content: EventShareContent,
): Uri? = try {
    val image = loadEventShareImage(context, content.imageUrl)
    if (image == null) null else withContext(Dispatchers.IO) {
        val directory = File(context.cacheDir, "event_share_previews").apply { mkdirs() }
        directory.listFiles()?.filter { System.currentTimeMillis() - it.lastModified() > 86_400_000L }
            ?.forEach { it.delete() }
        val file = File.createTempFile("crew-", ".jpg", directory)
        val encoded = file.outputStream().use { image.toBitmap().compress(Bitmap.CompressFormat.JPEG, 85, it) }
        if (!encoded) {
            file.delete()
            null
        } else {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        }
    }
} catch (exception: CancellationException) {
    throw exception
} catch (_: Exception) {
    // A missing image must never prevent the user from sharing the event link.
    null
}
