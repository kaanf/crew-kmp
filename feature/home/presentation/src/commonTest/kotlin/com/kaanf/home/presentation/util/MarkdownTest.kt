package com.kaanf.home.presentation.util

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MarkdownTest {

    @Test
    fun `bold italic and link markers are stripped from the text`() {
        val result = "**Loud** and _soft_ · [rules](https://crew.app)".toMarkdownAnnotatedString()

        assertEquals("Loud and soft · rules", result.text)
    }

    @Test
    fun `bold and italic spans cover the right ranges`() {
        val result = "**Loud** and _soft_".toMarkdownAnnotatedString()

        assertTrue(
            result.spanStyles.any {
                it.item.fontWeight == FontWeight.Bold && it.start == 0 && it.end == 4
            },
        )
        assertTrue(
            result.spanStyles.any {
                it.item.fontStyle == FontStyle.Italic && it.start == 9 && it.end == 13
            },
        )
    }

    @Test
    fun `headings bullets and emoji survive`() {
        val result = "# The night 🎉\n- doors 20:00\n* no plan".toMarkdownAnnotatedString()

        assertEquals("The night 🎉\n•  doors 20:00\n•  no plan", result.text)
    }

    @Test
    fun `plain text passes through untouched`() {
        val text = "Just a normal description, 100% plain."

        assertEquals(text, text.toMarkdownAnnotatedString().text)
    }
}
