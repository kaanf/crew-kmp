package com.kaanf.crew.screenshots

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class ScreenshotScene(val route: String) {
    Dashboard("dashboard"), EventDetail("event-detail"), Rps("rps"), Quest("quest"),
    Leaderboard("leaderboard"), TonightsRoll("tonights-roll");
    companion object {
        fun fromUri(uri: String): ScreenshotScene = entries.singleOrNull {
            uri == "crew-screenshot://${it.route}"
        } ?: error("Unknown screenshot route: $uri")
    }
}

object ScreenshotRouter {
    var scene by mutableStateOf(ScreenshotScene.Dashboard)
        private set
    var revision by mutableStateOf(0)
        private set
    fun open(uri: String) {
        scene = ScreenshotScene.fromUri(uri)
        revision++ // Reopening the same scene resets scroll, gestures and animations too.
    }
}
