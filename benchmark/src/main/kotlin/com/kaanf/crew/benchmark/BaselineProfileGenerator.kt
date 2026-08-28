package com.kaanf.crew.benchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test

/**
 * Cold start profilini üretir: Application.onCreate → Koin graph → Compose runtime →
 * splash → auth kontrolü → başlangıç ekranı (oturum yoksa Welcome).
 *
 * Çıktı: androidApp/src/release/generated/baselineProfiles/baseline-prof.txt
 * Üretim: ./gradlew :androidApp:generateReleaseBaselineProfile
 *
 * ponytail: login sonrası akış (Dashboard scroll) kapsanmıyor — test hesabı + canlı backend
 * gerektirir ve build'i backend'e bağımlı kılar. Cold start maliyetinin büyük kısmı zaten
 * oturum açılmadan önce ödeniyor. Ölçüp gerekirse ikinci bir journey olarak eklenir.
 */
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() = rule.collect(
        packageName = PACKAGE_NAME,
        // Journey zaten cold start; aynı kayıttan startup profilini de çıkar (R8 dex layout sıralaması).
        includeInStartupProfile = true,
    ) {
        pressHome()
        startActivityAndWait()

        // startActivityAndWait ilk kareyle döner, o kare splash olabilir. Auth kontrolü
        // DataStore'dan okuyup NavHost'u kurana kadar bekle.
        device.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), WINDOW_TIMEOUT_MS)
        device.waitForIdle(IDLE_TIMEOUT_MS)
    }

    private companion object {
        const val PACKAGE_NAME = "com.kaanf.crew"
        const val WINDOW_TIMEOUT_MS = 10_000L
        const val IDLE_TIMEOUT_MS = 5_000L
    }
}
