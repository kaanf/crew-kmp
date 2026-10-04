package com.kaanf.core.data.update

import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.requestAppUpdateInfo
import com.kaanf.core.domain.update.AppUpdateChecker
import kotlin.coroutines.cancellation.CancellationException

/**
 * Play, güncellemeyi yalnızca bu kullanıcıya gerçekten sunulabiliyorsa UPDATE_AVAILABLE der
 * (staged rollout dahil), bu yüzden mağazada "Güncelle" butonu olmayan birini kilitlemeyiz.
 * Play dışı kurulumlarda (Firebase App Distribution APK'sı) sorgu hata verir → null.
 */
class PlayAppUpdateChecker(
    private val context: Context,
) : AppUpdateChecker {
    override suspend fun storeUrlIfOutdated(): String? {
        val info = try {
            AppUpdateManagerFactory.create(context).requestAppUpdateInfo()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            return null
        }
        if (info.updateAvailability() != UpdateAvailability.UPDATE_AVAILABLE) return null
        return "https://play.google.com/store/apps/details?id=${context.packageName}"
    }
}
