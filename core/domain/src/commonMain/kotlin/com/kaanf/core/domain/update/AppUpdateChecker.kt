package com.kaanf.core.domain.update

interface AppUpdateChecker {
    /**
     * Mağazada bu kurulumdan yeni bir sürüm varsa mağaza sayfasının linki, yoksa null.
     * Sorgu başarısız olursa da null döner: kullanıcıyı belirsiz bir durumda kilitlemeyiz.
     */
    suspend fun storeUrlIfOutdated(): String?
}
