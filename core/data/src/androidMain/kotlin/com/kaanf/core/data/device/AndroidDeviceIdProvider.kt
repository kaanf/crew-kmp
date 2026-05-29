package com.kaanf.core.data.device

import android.content.Context
import com.kaanf.core.domain.provider.DeviceIdProvider
import java.util.UUID
import androidx.core.content.edit

private const val DEVICE_PREFS_NAME = "device_prefs"
private const val DEVICE_ID_KEY = "installation_id"

class AndroidDeviceIdProvider(
    private val context: Context,
) : DeviceIdProvider {
    override fun getDeviceId(): String = getAndroidDeviceId(context)

    fun getRandomizedDeviceId(): String = UUID.randomUUID().toString()
}

fun getAndroidDeviceId(context: Context): String =
    context
        .getSharedPreferences(DEVICE_PREFS_NAME, Context.MODE_PRIVATE)
        .let { preferences ->
            preferences.getString(DEVICE_ID_KEY, null)
                ?: UUID.randomUUID().toString().also { generatedId ->
                    preferences.edit { putString(DEVICE_ID_KEY, generatedId) }
                }
        }
