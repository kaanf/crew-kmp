package com.kaanf.game.presentation.quests

import com.kaanf.game.domain.model.Quest

internal enum class QuestFamily { Meet, Streak, Beat, Rematch, Revenge, Other }

internal enum class QuestCategory {
    All, Meet, Wins, Rematch;

    fun includes(family: QuestFamily): Boolean = when (this) {
        All -> true
        Meet -> family == QuestFamily.Meet
        Wins -> family == QuestFamily.Streak || family == QuestFamily.Beat
        Rematch -> family == QuestFamily.Rematch || family == QuestFamily.Revenge
    }
}

internal data class QuestGroup(
    val key: String,
    val family: QuestFamily,
    val levels: List<Quest>,
) {
    // An uncollected reward remains visible even when later milestones are also complete.
    val current: Quest get() = levels.firstOrNull { !it.claimed } ?: levels.last()
    val collected: Boolean get() = levels.all { it.claimed }
    val claimable: Boolean get() = levels.any { it.completed && !it.claimed }
}

internal fun groupQuests(quests: List<Quest>): List<QuestGroup> = quests.groupBy { quest ->
    // Only known catalog families are merged. Custom quests retain their own identity.
    when {
        quest.key.matches(Regex("meet-\\d+")) -> "family:meet" to QuestFamily.Meet
        quest.key.matches(Regex("win-streak-\\d+")) -> "family:streak" to QuestFamily.Streak
        quest.key.matches(Regex("beat-\\d+")) -> "family:beat" to QuestFamily.Beat
        quest.key == "rematch-master" -> "family:rematch" to QuestFamily.Rematch
        quest.key == "revenge-win" -> "family:revenge" to QuestFamily.Revenge
        else -> "quest:${quest.key}" to QuestFamily.Other
    }
}.map { (identity, levels) ->
    QuestGroup(identity.first, identity.second, levels.sortedBy { it.target })
}
