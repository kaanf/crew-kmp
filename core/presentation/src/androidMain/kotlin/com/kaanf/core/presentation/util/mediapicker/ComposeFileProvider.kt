package com.kaanf.core.presentation.util.mediapicker

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

// Creates a content:// URI in the app cache that the camera can write the full-resolution
// capture into. Requires a matching <provider> with authority "${packageName}.fileprovider"
// plus a file_paths resource in the host app manifest before the camera launcher is wired up.
internal object ComposeFileProvider {
    fun createImageUri(context: Context): Uri {
        val capturesDir = File(context.cacheDir, "camera_captures").apply { mkdirs() }
        val file = File.createTempFile("capture_", ".jpg", capturesDir)
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(context, authority, file)
    }
}
