package com.kaanf.core.domain.review

/**
 * Store'un kendi puanlama diyaloğunu ister.
 *
 * Gösterilip gösterilmeyeceğine platform karar verir — iOS yılda 3 gösterimle sınırlar,
 * kararını bildirmez ve sonucu sorgulamanın bir yolu yoktur. Bu yüzden çağıran taraf
 * hiçbir şey beklememeli: çağır ve akışına devam et, navigasyonu buna bağlama.
 */
expect fun requestAppReview()
