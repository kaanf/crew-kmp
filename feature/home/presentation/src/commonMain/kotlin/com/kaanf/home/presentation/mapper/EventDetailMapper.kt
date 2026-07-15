package com.kaanf.home.presentation.mapper

import com.kaanf.core.domain.model.event.EventDetail
import com.kaanf.home.presentation.model.EventDetailUiModel
import com.kaanf.home.presentation.util.toClockText
import com.kaanf.home.presentation.util.toEventDateText

fun EventDetail.toUiModel(): EventDetailUiModel {
    return EventDetailUiModel(
        id = id,
        title = title,
        description = description,
        heroDate = startsAt.toEventDateText(),
        doorsOpenAt = doorsOpenAt,
        hasMyTicket = hasMyTicket,
        gameTime = "${startsAt.toClockText()} - ${endsAt.toClockText()}",
        crew = "$soldCount / $capacity in",
        formattedPrice = price.format(),
        isFree = price.amount == 0L,
        // Backend EventPhase.name gönderir: NotOpened / EntryOpen / Gameplay / Finished.
        isStarted = phase == "Gameplay",
        isEnded = phase == "Finished",
    )
}
