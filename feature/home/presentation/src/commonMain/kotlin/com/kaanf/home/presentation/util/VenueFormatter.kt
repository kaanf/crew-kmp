package com.kaanf.home.presentation.util

import com.kaanf.core.domain.model.venue.Venue

fun Venue.toFormattedAddress(): String {
    return "${this.name} · ${this.district}"
}
