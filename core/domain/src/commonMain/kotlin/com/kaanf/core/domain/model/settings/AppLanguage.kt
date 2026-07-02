package com.kaanf.core.domain.model.settings

enum class AppLanguage(val tag: String) {
    ENGLISH("en"),
    CZECH("cs");

    companion object {
        val DEFAULT: AppLanguage = ENGLISH

        fun fromTag(tag: String?): AppLanguage =
            entries.firstOrNull { it.tag == tag } ?: DEFAULT
    }
}
