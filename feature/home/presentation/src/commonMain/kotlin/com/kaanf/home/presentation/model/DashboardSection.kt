package com.kaanf.home.presentation.model

import androidx.compose.runtime.Composable

data class DashboardSection(
    val info: DashboardSectionInfo,
    val content: @Composable () -> Unit,
)

data class DashboardSectionInfo(
    val title: String,
    val description: String? = null,
    val ctaText: String,
)
