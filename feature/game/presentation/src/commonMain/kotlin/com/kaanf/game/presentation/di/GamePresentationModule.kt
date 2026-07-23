package com.kaanf.game.presentation.di

import com.kaanf.game.presentation.history.HistoryViewModel
import com.kaanf.game.presentation.leaderboard.LeaderboardViewModel
import com.kaanf.game.presentation.memories.MemoriesViewModel
import com.kaanf.game.presentation.quests.QuestsViewModel
import com.kaanf.game.presentation.scanopponent.ScanOpponentViewModel
import com.kaanf.game.presentation.session.MatchSessionViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val gamePresentationModule =
    module {
        // Maç oturumunun graph-scoped tek sahibi (socket + lobi + faz + paylaşılan state).
        viewModelOf(::MatchSessionViewModel)
        viewModelOf(::ScanOpponentViewModel)
        viewModelOf(::LeaderboardViewModel)
        viewModelOf(::HistoryViewModel)
        viewModelOf(::MemoriesViewModel)
        viewModelOf(::QuestsViewModel)
    }
