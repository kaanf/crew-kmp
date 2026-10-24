package com.kaanf.game.presentation.quests

import com.kaanf.game.domain.model.Quest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class QuestGroupTest {
    @Test
    fun categoryFiltersKeepEveryKnownFamilyDiscoverableWithoutDuplicatingIt() {
        QuestFamily.entries.filter { it != QuestFamily.Other }.forEach { family ->
            assertTrue(QuestCategory.All.includes(family))
            assertEquals(1, QuestCategory.entries.count { it != QuestCategory.All && it.includes(family) })
        }
        assertTrue(QuestCategory.Wins.includes(QuestFamily.Beat))
        assertTrue(QuestCategory.Rematch.includes(QuestFamily.Revenge))
    }

    @Test
    fun customQuestsRemainVisibleInAllPaths() {
        val group = groupQuests(listOf(quest("custom-event-quest", 1))).single()
        assertTrue(QuestCategory.All.includes(group.family))
    }

    @Test
    fun milestonesShareOneGroupAndKeepTheirIndividualRewards() {
        val groups = groupQuests(listOf(quest("meet-15", 15), quest("meet-5", 5), quest("meet-10", 10)))
        assertEquals(1, groups.size)
        assertEquals(listOf(5, 10, 15), groups.single().levels.map { it.target })
        assertEquals(3, groups.single().levels.map { it.key }.distinct().size)
    }

    @Test
    fun uncollectedEarlierRewardStaysCurrentEvenWhenAllMilestonesAreComplete() {
        val group = groupQuests(listOf(
            quest("meet-5", 5, completed = true),
            quest("meet-10", 10, completed = true),
        )).single()
        assertEquals("meet-5", group.current.key)
        assertTrue(group.claimable)
        assertFalse(group.collected)
    }

    @Test
    fun collectingOneLevelAdvancesWithoutMarkingTheWholeFamilyCollected() {
        val group = groupQuests(listOf(
            quest("win-streak-3", 3, completed = true, claimed = true),
            quest("win-streak-5", 5),
        )).single()
        assertEquals("win-streak-5", group.current.key)
        assertFalse(group.claimable)
        assertFalse(group.collected)
    }

    @Test
    fun fullyCollectedFamilyDisplaysItsHighestLevel() {
        val group = groupQuests(listOf(
            quest("meet-5", 5, completed = true, claimed = true),
            quest("meet-10", 10, completed = true, claimed = true),
        )).single()
        assertTrue(group.collected)
        assertEquals("meet-10", group.current.key)
    }

    @Test
    fun unrelatedCustomQuestsAreNotMergedBecauseTheyShareTargetsOrTitles() {
        assertEquals(4, groupQuests(listOf(
            quest("custom-one", 5), quest("custom-two", 5),
            quest("meet-5", 5), quest("win-streak-5", 5),
        )).size)
    }

    private fun quest(key: String, target: Int, completed: Boolean = false, claimed: Boolean = false) =
        Quest(key, "A quest", "A description", 10, target, if (completed) target else 0, completed, claimed)
}
