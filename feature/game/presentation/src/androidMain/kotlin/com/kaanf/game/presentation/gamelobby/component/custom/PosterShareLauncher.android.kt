package com.kaanf.game.presentation.gamelobby.component.custom

import android.content.ClipData
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
internal actual fun rememberPosterShareLauncher(): suspend (ByteArray) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        { bytes ->
            val file = withContext(Dispatchers.IO) {
                val directory = File(context.cacheDir, "lobby_posters").apply { mkdirs() }
                // Retain recent exports for receiving apps; bound old cache files on the next export.
                directory.listFiles()?.filter { System.currentTimeMillis() - it.lastModified() > 86_400_000L }
                    ?.forEach { it.delete() }
                File.createTempFile("crew-", ".jpg", directory).apply { writeBytes(bytes) }
            }
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, uri)
                clipData = ClipData.newRawUri("Crew poster", uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, null))
        }
    }
}
