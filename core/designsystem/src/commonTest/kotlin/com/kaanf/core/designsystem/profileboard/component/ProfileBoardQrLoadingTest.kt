package com.kaanf.core.designsystem.profileboard.component

import com.kaanf.core.domain.model.profileboard.*
import kotlin.test.*

class ProfileBoardQrLoadingTest {
    private val theme = BoardQrTheme.HAMSTER.id

    @Test fun savingAndPendingRenderingKeepTheLoaderVisible() {
        assertTrue(shouldShowQrLoading(theme, null, true, true, false, false))
        assertTrue(shouldShowQrLoading(theme, BoardQrRender(theme, BoardQrRenderStatus.PENDING), false, false, false, false))
    }

    @Test fun readyWaitsForTheActualImageDownload() {
        val render = BoardQrRender(theme, BoardQrRenderStatus.READY, "https://cdn.test/qr.png")
        assertTrue(shouldShowQrLoading(theme, render, false, false, false, false))
        assertFalse(shouldShowQrLoading(theme, render, false, false, true, false))
    }

    @Test fun anOldThemeCannotFinishLoadingTheNewSelection() {
        val stale = BoardQrRender(BoardQrTheme.VAMPIRE.id, BoardQrRenderStatus.READY, "https://cdn.test/old.png")
        assertTrue(shouldShowQrLoading(theme, stale, false, false, false, false))
    }

    @Test fun terminalFailuresEndLoadingAndAllowTheRegularQr() {
        assertFalse(shouldShowQrLoading(theme, BoardQrRender(theme, BoardQrRenderStatus.FALLBACK), false, false, false, false))
        assertFalse(shouldShowQrLoading(theme, BoardQrRender(theme, BoardQrRenderStatus.NONE), false, false, false, false))
        assertFalse(shouldShowQrLoading(theme, BoardQrRender(theme, BoardQrRenderStatus.READY), false, false, false, false))
        assertFalse(shouldShowQrLoading(theme, null, false, false, false, true))
    }

    @Test fun plainQrAndEditingPreviewsDoNotShowTheLoader() {
        assertFalse(shouldShowQrLoading("rounded", null, false, false, false, false))
        assertFalse(shouldShowQrLoading(theme, null, true, false, false, false))
    }
}
