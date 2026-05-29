package com.kaanf.core.designsystem.util

import com.kaanf.core.designsystem.component.layout.CustomSnackbarVariant
import com.kaanf.core.presentation.model.SnackbarVariant

fun SnackbarVariant.toCustomSnackbarVariant(): CustomSnackbarVariant {
    return when (this) {
        SnackbarVariant.Success -> CustomSnackbarVariant.Success
        SnackbarVariant.Failure -> CustomSnackbarVariant.Failure
        SnackbarVariant.Warning -> CustomSnackbarVariant.Warning
    }
}
