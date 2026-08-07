package com.kaanf.home.presentation.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.em
import com.kaanf.core.designsystem.theme.AccessDefaults

/**
 * Admin panelinden gelen serbest metni basit markdown olarak render eder.
 * Emoji ayrı iş istemez; sistem font fallback'i halleder.
 *
 * ponytail: kasıtlı olarak alt küme — başlık, madde, kalın/italik, link.
 * Tablo/kod bloğu/iç içe liste gerekirse tam bir markdown kütüphanesi eklenir.
 */
fun String.toMarkdownAnnotatedString(): AnnotatedString = buildAnnotatedString {
    trim().lines().forEachIndexed { index, rawLine ->
        if (index > 0) append('\n')

        val line = rawLine.trim()
        val heading = HEADING_SIZES.entries.firstOrNull { line.startsWith(it.key) }

        when {
            heading != null -> withStyle(
                SpanStyle(fontWeight = FontWeight.Bold, fontSize = heading.value.em),
            ) { appendInline(line.removePrefix(heading.key).trim()) }

            BULLET_PREFIXES.any { line.startsWith(it) } -> {
                append(BULLET)
                appendInline(line.drop(2))
            }

            else -> appendInline(line)
        }
    }
}

private fun AnnotatedString.Builder.appendInline(text: String) {
    var cursor = 0

    INLINE_PATTERN.findAll(text).forEach { match ->
        append(text.substring(cursor, match.range.first))
        cursor = match.range.last + 1

        val (bold, boldAlt, italic, italicAlt, linkText, linkUrl) = match.destructured

        when {
            bold.isNotEmpty() || boldAlt.isNotEmpty() ->
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(bold.ifEmpty { boldAlt })
                }

            italic.isNotEmpty() || italicAlt.isNotEmpty() ->
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(italic.ifEmpty { italicAlt })
                }

            else -> withLink(
                LinkAnnotation.Url(
                    url = linkUrl,
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = AccessDefaults.Accent,
                            textDecoration = TextDecoration.Underline,
                        ),
                    ),
                ),
            ) { append(linkText) }
        }
    }

    append(text.substring(cursor))
}

private const val BULLET = "•  "
private val BULLET_PREFIXES = listOf("- ", "* ")
private val HEADING_SIZES = linkedMapOf("### " to 1.05f, "## " to 1.15f, "# " to 1.3f)

private val INLINE_PATTERN = Regex(
    """\*\*(.+?)\*\*|__(.+?)__|\*(.+?)\*|_(.+?)_|\[([^\]]+)]\((\S+?)\)""",
)
