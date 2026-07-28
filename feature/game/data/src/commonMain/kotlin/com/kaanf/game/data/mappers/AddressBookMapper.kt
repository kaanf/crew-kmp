package com.kaanf.game.data.mappers

import com.kaanf.game.data.dto.AddressBookDto
import com.kaanf.game.data.dto.AddressBookEntryDto
import com.kaanf.game.domain.model.AddressBook
import com.kaanf.game.domain.model.AddressBookEntry
import com.kaanf.game.domain.model.AddressBookTitle
import kotlin.time.Instant

fun AddressBookDto.toDomain(): AddressBook = AddressBook(
    totalCount = totalCount,
    entries = entries.map { it.toDomain() },
)

fun AddressBookEntryDto.toDomain(): AddressBookEntry = AddressBookEntry(
    userId = userId,
    fullName = fullName,
    profilePictureUrl = profilePictureUrl,
    metAt = Instant.parse(metAt),
    firstMatchWon = firstMatchWon,
    firstMatchTaskTitle = firstMatchTaskTitle,
    title = title?.let { AddressBookTitle(key = it.key, label = it.label, emoji = it.emoji) },
)
