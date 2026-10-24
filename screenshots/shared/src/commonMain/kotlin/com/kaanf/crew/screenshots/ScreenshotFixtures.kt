package com.kaanf.crew.screenshots

import com.kaanf.game.domain.model.*
import com.kaanf.game.presentation.leaderboard.LeaderboardState
import com.kaanf.game.presentation.memories.MemoriesState
import com.kaanf.game.presentation.session.MatchPhase
import com.kaanf.game.presentation.session.MatchSessionState
import com.kaanf.home.domain.model.EventGuest
import com.kaanf.home.presentation.dashboard.DashboardState
import com.kaanf.home.presentation.eventdetail.EventDetailState
import com.kaanf.home.presentation.model.*
import kotlin.time.Instant
import org.koin.dsl.module

/** Immutable presentation fixtures: no clock, repositories, sessions or production DI modules. */
class ScreenshotFixtures {
    val eventId = "demo-event-2026-10-24"
    val userId = "demo-player-04"
    val names = listOf("Maya Chen", "Leo Rivera", "Zoe Martin", "Alex Morgan", "Sam Patel", "Ruby Ellis", "Noah Kim", "Isla Brooks")
    fun avatar(index: Int) = "fixture://avatar-${index + 1}.png"
    val cover = "fixture://event.png"
    val event = EventDashboardUiModel(
        id = eventId, title = "The Social Club", date = "SAT, OCT 24 · 7:30 PM",
        percentage = 78, formattedPrice = "€12", isFeatured = true, hasMyTicket = true,
        imageUrl = cover, dayOfMonth = "24", monthShort = "OCT", doorsTime = "19:30",
    )
    val dashboard = DashboardState(
        isLoading = false, userName = names[3], profilePictureUrl = avatar(3),
        featuredEvents = listOf(event), myEvents = listOf(event),
        upcomingEvents = listOf(event.copy(id = "demo-event-2026-10-31", title = "Strangers & Friends", hasMyTicket = false, dayOfMonth = "31", date = "SAT, OCT 31 · 7:30 PM")),
    )
    val detail = EventDetailState(
        event = EventDetailUiModel(
            id = eventId, title = event.title,
            description = "Good people. Unexpected challenges. A night worth remembering. Meet your next crew over face-to-face games at The Lantern.",
            heroDate = "Saturday, October 24", doorsTime = "19:30", hasMyTicket = true,
            formattedPrice = "€12", isFree = false, isEnded = false, isLocked = false,
            imageUrls = listOf(cover),
            location = EventLocationUiModel("The Lantern", "24 Lantern Lane", "Old Town", "Prague", 50.087, 14.421),
        ),
        guests = names.mapIndexed { i, name -> EventGuest("demo-player-0${i + 1}", name, avatar(i), i < 5) },
    )
    val task = GameTask(
        id = "demo-task-icebreaker-01",
        title = "Find someone wearing your favourite colour. Ask them for the story behind it.",
        points = 20, rejectPoints = -5, category = TaskCategory.Icebreaker,
    )
    private val match = MatchSessionState(
        connectionState = GameConnectionState.Connected,
        currentUserId = userId, currentUserName = names[3], currentUserPhotoUrl = avatar(3),
        currentUserScore = 185, currentUserWinCount = 7, currentUserMatchesCount = 10,
        matchId = "demo-match-01", opponentUserId = "demo-player-01",
        opponentFullName = names[0], opponentProfilePictureUrl = avatar(0),
    )
    val rps = match.copy(phase = MatchPhase.RpsReady())
    val quest = match.copy(phase = MatchPhase.TaskActive(), activeTask = task, amIWinner = false)
    val ended = match.copy(phase = MatchPhase.Idle, isGameEnded = true)
    val leaderboard = LeaderboardState(
        isLoading = false, currentUserId = userId, isConfettiPending = false,
        entries = names.mapIndexed { i, name ->
            LeaderboardEntry(i + 1, "demo-player-0${i + 1}", name, avatar(i), listOf(260, 235, 210, 185, 170, 155, 140, 125)[i])
        },
    )
    val memories = MemoriesState(
        isLoading = false, endReached = true,
        memories = (1..4).map { i ->
            EventMemory(
                id = "demo-memory-0$i", imageUrl = "fixture://memory-$i.png",
                ownerName = names[3], ownerProfilePictureUrl = avatar(3), isMine = true,
                questKey = null, taskTitle = "Find your crew. Strike your best album-cover pose.",
                tagged = emptyList(), capturedAt = Instant.parse("2026-10-24T20:15:00Z"),
            )
        },
    )
}

val screenshotModule = module { single { ScreenshotFixtures() } }
