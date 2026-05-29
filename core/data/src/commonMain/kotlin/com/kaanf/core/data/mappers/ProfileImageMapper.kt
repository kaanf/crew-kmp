package com.kaanf.core.data.mappers

private const val ProfileImageBaseUrl = "https://ads.kaanf.com/profile/"
private const val ProfileImageExtension = ".png"

fun String.toProfileImageUrl(): String {
    if (isBlank()) return this
    if (startsWith("http://") || startsWith("https://")) return this

    val normalizedSuffix =
        removeSuffix(ProfileImageExtension)
            .substringAfterLast("/")

    return "$ProfileImageBaseUrl$normalizedSuffix$ProfileImageExtension"
}

fun String.toProfileImageSuffix(): String {
    if (isBlank()) return this

    return substringAfterLast("/")
        .removeSuffix(ProfileImageExtension)
}
