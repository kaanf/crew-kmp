package com.kaanf.core.presentation.profileboard

import com.kaanf.core.domain.model.profileboard.*
import com.kaanf.core.domain.model.user.User
import com.kaanf.core.domain.repository.ProfileBoardRepository
import com.kaanf.core.domain.repository.UserStore
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.SnackbarMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileBoardQrMonitorTest {
    private val currentUser = MutableStateFlow<User?>(user("first"))
    private val users = object : UserStore {
        override fun observeCurrentUser() = currentUser
        override suspend fun updateCurrentUser(user: User) { currentUser.value = user }
    }
    private val repository = FakeBoardRepository()
    private val snackbar = SnackbarController()
    private val monitor = ProfileBoardQrMonitor(repository, users, snackbar)

    @Test
    fun pendingSurvivesPollingBatchesAndDisappearsWhenReady() = runTest {
        monitor.start(backgroundScope)
        runCurrent()
        assertNotNull(snackbar.persistent.value)
        advanceTimeBy(240_000)
        runCurrent()
        assertNotNull(snackbar.persistent.value)
        assertTrue(repository.syncCalls > 60)
        repository.nextStatus = BoardQrRenderStatus.READY
        advanceTimeBy(6_000)
        runCurrent()
        assertNull(snackbar.persistent.value)
    }

    @Test
    fun ordinaryErrorsDoNotReplaceTheProcessingStateAndRetriesCanFinish() = runTest {
        val messages = mutableListOf<SnackbarMessage>()
        backgroundScope.launch { snackbar.messages.collect { messages += it } }
        repository.fail = true
        monitor.start(backgroundScope)
        runCurrent()
        advanceTimeBy(20_000)
        runCurrent()
        assertNotNull(snackbar.persistent.value)
        assertEquals(1, messages.size)
        repository.fail = false
        repository.nextStatus = BoardQrRenderStatus.READY
        advanceTimeBy(6_000)
        runCurrent()
        assertNull(snackbar.persistent.value)
    }

    @Test
    fun fallbackDismissesProgressAndReportsOneWarning() = runTest {
        val messages = mutableListOf<SnackbarMessage>()
        backgroundScope.launch { snackbar.messages.collect { messages += it } }
        repository.nextStatus = BoardQrRenderStatus.FALLBACK
        monitor.start(backgroundScope)
        runCurrent()
        advanceTimeBy(2_000)
        runCurrent()
        assertNull(snackbar.persistent.value)
        assertEquals(1, messages.size)
        advanceTimeBy(30_000)
        runCurrent()
        assertEquals(1, messages.size)
    }

    @Test
    fun logoutCancelsPollingAndDismissesProcessing() = runTest {
        monitor.start(backgroundScope)
        runCurrent()
        assertNotNull(snackbar.persistent.value)
        currentUser.value = null
        runCurrent()
        assertNull(snackbar.persistent.value)
        val calls = repository.syncCalls
        advanceTimeBy(30_000)
        runCurrent()
        assertEquals(calls, repository.syncCalls)
    }

    @Test
    fun choosingAPlainQrCancelsTheOldThemeAndDismissesProcessing() = runTest {
        monitor.start(backgroundScope)
        runCurrent()
        assertNotNull(snackbar.persistent.value)
        repository.board.value = ProfileBoard(revision = 2, qrStyleId = "rounded")
        runCurrent()
        assertNull(snackbar.persistent.value)
        val calls = repository.syncCalls
        advanceTimeBy(30_000)
        runCurrent()
        assertEquals(calls, repository.syncCalls)
    }

    @Test
    fun changingThemeKeepsProgressUntilTheNewThemeArrives() = runTest {
        monitor.start(backgroundScope)
        runCurrent()
        repository.board.value = ProfileBoard(revision = 2, qrStyleId = BoardQrTheme.VAMPIRE.id)
        runCurrent()
        repository.nextStatus = BoardQrRenderStatus.READY
        advanceTimeBy(6_000)
        runCurrent()
        assertEquals(BoardQrTheme.VAMPIRE.id, repository.render.value?.themeId)
        assertNull(snackbar.persistent.value)
    }

    private fun user(id: String) = User(id, "test@example.invalid", "Test",
        role = "User", status = "Active", isProfileComplete = true, hasVerifiedEmail = true)

    private class FakeBoardRepository : ProfileBoardRepository {
        val board = MutableStateFlow<ProfileBoard?>(ProfileBoard(qrStyleId = BoardQrTheme.HAMSTER.id))
        val render = MutableStateFlow<BoardQrRender?>(null)
        var nextStatus = BoardQrRenderStatus.PENDING
        var syncCalls = 0
        var fail = false
        override fun observeBoard() = board
        override fun observeQrRender() = render
        override suspend fun refresh() = Result.Success(board.value!!)
        override suspend fun save(board: ProfileBoard) = Result.Success(board).also { this.board.value = board }
        override suspend fun recoverQrRender() = syncQrRender()
        override suspend fun syncQrRender(): Result<BoardQrRender, DataError.Remote> {
            syncCalls++
            delay(1_000)
            if (fail) return Result.Failure(DataError.Remote.NO_INTERNET)
            val result = BoardQrRender(board.value!!.qrStyleId, nextStatus,
                imageUrl = if (nextStatus == BoardQrRenderStatus.READY) "https://cdn.test/qr.png" else null)
            render.value = result
            return Result.Success(result)
        }
    }
}
