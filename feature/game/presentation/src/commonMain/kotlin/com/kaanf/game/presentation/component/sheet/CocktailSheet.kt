package com.kaanf.game.presentation.component.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.image.BaseImage
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.game.domain.model.AnnouncementCocktail
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.cocktail_note_finish
import crew.feature.game.presentation.generated.resources.cocktail_note_nose
import crew.feature.game.presentation.generated.resources.cocktail_note_palate
import crew.feature.game.presentation.generated.resources.cocktail_notes_kicker
import crew.feature.game.presentation.generated.resources.cocktail_offer_hint
import crew.feature.game.presentation.generated.resources.cocktail_photo_placeholder
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

// Barın pembe ışığı; duyuru chip'i de aynı rengi kullandığı için design system'de.
private val CocktailPink = AccessDefaults.Blush
private val CocktailPinkSoft = AccessDefaults.Blush.copy(alpha = 0.16f)

/**
 * Duyuru chip'ine tıklanınca açılan iki sayfalık kokteyl sheet'i: hikâye ve tat notları.
 * Alt şerit (indirim satırı + sayfa noktaları) sayfalar arasında sabit kalır.
 */
@Composable
fun CocktailSheet(
    cocktail: AnnouncementCocktail,
    offerTitle: String?,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { 2 })

    Column(
        modifier = modifier.fillMaxWidth().fillMaxHeight(0.86f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        VenueHeader(venueName = cocktail.venueName)

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            pageSpacing = 8.dp,
        ) { page ->
            if (page == 0) {
                StoryPage(cocktail = cocktail)
            } else {
                NotesPage(cocktail = cocktail)
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 34.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            offerTitle?.takeIf { it.isNotBlank() }?.let { title ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = CocktailPink,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                        ),
                    )
                    Text(
                        text = stringResource(Res.string.cocktail_offer_hint),
                        style = kickerStyle(color = AccessDefaults.TextMuted, letterSpacing = 1.sp),
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                repeat(2) { index ->
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(
                                color = if (pagerState.currentPage == index) {
                                    CocktailPink
                                } else {
                                    AccessDefaults.TextFaint
                                },
                                shape = CircleShape,
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun VenueHeader(venueName: String) {
    Row(
        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .background(color = CocktailPinkSoft, shape = RoundedCornerShape(8.dp))
                .border(width = 1.dp, color = CocktailPink.copy(alpha = 0.32f), shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = venueName.initials(),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CocktailPink,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
        Text(
            text = venueName.uppercase(),
            style = kickerStyle(color = AccessDefaults.TextSecondary, letterSpacing = 2.6.sp),
        )
    }
}

@Composable
private fun StoryPage(cocktail: AnnouncementCocktail) {
    Column(
        // Uzun hikâye/notlar taşarsa sayfa kendi içinde kaysın; sığdığında dikeyde ortalanır.
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 38.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(22.dp, Alignment.CenterVertically),
    ) {
        CocktailPhoto(imageUrl = cocktail.imageUrl)

        cocktail.tagline?.let { RuledLabel(text = it) }

        Text(
            text = cocktail.name,
            style = MaterialTheme.typography.displaySmall.copy(
                color = AccessDefaults.TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 36.sp,
                textAlign = TextAlign.Center,
            ),
        )

        Text(
            text = cocktail.story,
            modifier = Modifier.widthIn(max = 296.dp),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = AccessDefaults.TextSecondary,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
            ),
        )

        cocktail.signature?.let {
            Text(
                text = "— $it",
                style = kickerStyle(color = AccessDefaults.TextFaint, letterSpacing = 1.8.sp),
            )
        }
    }
}

@Composable
private fun NotesPage(cocktail: AnnouncementCocktail) {
    Column(
        // Uzun hikâye/notlar taşarsa sayfa kendi içinde kaysın; sığdığında dikeyde ortalanır.
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 38.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(22.dp, Alignment.CenterVertically),
    ) {
        Text(
            text = stringResource(Res.string.cocktail_notes_kicker).uppercase(),
            style = kickerStyle(color = CocktailPink.copy(alpha = 0.72f), letterSpacing = 2.8.sp),
        )

        Text(
            text = cocktail.name,
            style = MaterialTheme.typography.titleLarge.copy(
                color = AccessDefaults.TextPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
        )

        Rule()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(26.dp),
        ) {
            NoteRow(label = stringResource(Res.string.cocktail_note_nose), value = cocktail.nose)
            NoteRow(label = stringResource(Res.string.cocktail_note_palate), value = cocktail.palate)
            NoteRow(label = stringResource(Res.string.cocktail_note_finish), value = cocktail.finish)
        }

        Rule()

        cocktail.servingNote?.let {
            Text(
                text = it,
                modifier = Modifier.widthIn(max = 250.dp),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AccessDefaults.TextMuted,
                    lineHeight = 21.sp,
                    textAlign = TextAlign.Center,
                ),
            )
        }
    }
}

@Composable
private fun CocktailPhoto(imageUrl: String?) {
    Box(
        modifier = Modifier
            .size(184.dp)
            .clip(CircleShape)
            .background(AccessDefaults.SurfaceHigh),
        contentAlignment = Alignment.Center,
    ) {
        if (imageUrl != null) {
            BaseImage(
                imageUrl = imageUrl,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            )
        } else {
            Text(
                text = stringResource(Res.string.cocktail_photo_placeholder).uppercase(),
                style = kickerStyle(color = AccessDefaults.TextFaint, letterSpacing = 2.sp),
            )
        }
    }
}

/** Metnin iki yanında kısa çizgi olan küçük etiket (tasarımdaki "Never on the menu"). */
@Composable
private fun RuledLabel(text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SideLine()
        Text(
            text = text.uppercase(),
            style = kickerStyle(color = CocktailPink, letterSpacing = 2.6.sp),
        )
        SideLine()
    }
}

@Composable
private fun SideLine() {
    Box(
        modifier = Modifier
            .width(14.dp)
            .height(1.dp)
            .background(CocktailPink.copy(alpha = 0.5f)),
    )
}

@Composable
private fun Rule() {
    Box(
        modifier = Modifier
            .width(34.dp)
            .height(1.dp)
            .background(AccessDefaults.TextPrimary.copy(alpha = 0.14f)),
    )
}

@Composable
private fun NoteRow(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label.uppercase(),
            style = kickerStyle(color = AccessDefaults.TextFaint, letterSpacing = 2.6.sp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                color = AccessDefaults.TextPrimary,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

/** Tasarımdaki mono, harf aralıklı, büyük harf küçük etiket stili. */
@Composable
private fun kickerStyle(color: Color, letterSpacing: androidx.compose.ui.unit.TextUnit): TextStyle =
    MaterialTheme.typography.labelSmall.copy(
        color = color,
        fontSize = 9.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = letterSpacing,
        textAlign = TextAlign.Center,
    )

private fun String.initials(): String =
    trim().split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { "?" }

@Preview
@Composable
fun CocktailSheetPreview() {
    CrewTheme {
        CocktailSheet(
            offerTitle = "15% off at the bar",
            cocktail = AnnouncementCocktail(
                name = "Noční Hlídka",
                venueName = "Bar Skutek",
                tagline = "Never on the menu",
                story = "One winter night in 2019 the bar was closing and one table would not " +
                    "leave. Vít mixed something for the ones who stayed — bourbon, sour cherry, " +
                    "a pinch of salt — and called it the night watch.",
                signature = "Vít, bar lead",
                imageUrl = null,
                nose = "Sour cherry, warm oak",
                palate = "Bourbon, dark honey",
                finish = "Salt, long and dry",
                servingNote = "Served up, no garnish. Best while the room is still loud.",
            ),
        )
    }
}
