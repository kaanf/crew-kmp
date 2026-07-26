package com.kaanf.home.presentation.mapper

import com.kaanf.core.domain.model.event.EventDetail
import com.kaanf.core.domain.model.venue.Venue
import com.kaanf.home.presentation.model.EventDetailUiModel
import com.kaanf.home.presentation.model.EventLocationUiModel
import com.kaanf.home.presentation.util.toClockText
import com.kaanf.home.presentation.util.toEventDetailDateText

fun EventDetail.toUiModel(): EventDetailUiModel {
    return EventDetailUiModel(
        id = id,
        title = title,
        description = description,
        heroDate = startsAt.toEventDetailDateText(),
        doorsOpenAt = doorsOpenAt,
        hasMyTicket = hasMyTicket,
        gameTime = "${startsAt.toClockText()} - ${endsAt.toClockText()}",
        crew = "$soldCount / $capacity in",
        formattedPrice = price.format(),
        isFree = price.amount == 0L,
        // Backend EventPhase.name gönderir: NotOpened / EntryOpen / Gameplay / Finished.
        isEnded = phase == "Finished",
        // ponytail: backend görsel dönmezse eski sabit hero görseline düşülür; backend dolunca ölü kod olur
        imageUrls = imageUrls.ifEmpty { listOf(FALLBACK_HERO_IMAGE_URL) },
        location = venue?.toLocationUiModel(),
    )
}

internal fun Venue.toLocationUiModel(): EventLocationUiModel? {
    val latitude = latitude ?: return null
    val longitude = longitude ?: return null

    return EventLocationUiModel(
        name = name,
        address = "$address, $district / $city",
        latitude = latitude,
        longitude = longitude,
    )
}

private const val FALLBACK_HERO_IMAGE_URL =
    "https://hostel-drunken-monkey.praguehotelsweb.com/data/Photos/OriginalPhoto/16920/1692044/1692044305/drunken-monkey-hostel-prague-photo-15.JPEG"
