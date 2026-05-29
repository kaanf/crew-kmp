package com.kaanf.core.presentation.model

import com.kaanf.core.presentation.util.UIText

data class SnackbarMessage(
    val title: UIText,
    val description: UIText,
    val variant: SnackbarVariant
)

enum class SnackbarVariant {
    Success, Failure, Warning
}
