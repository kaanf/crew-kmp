package com.kaanf.game.presentation.passport

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.FullScreenLoader
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.dottedBorder
import com.kaanf.game.presentation.passport.component.PassportPageCard
import com.kaanf.game.presentation.passport.component.PassportRareStampRow
import com.kaanf.game.presentation.passport.component.PassportStampDetailCard
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.passport_collect_action
import crew.feature.game.presentation.generated.resources.passport_eyebrow
import crew.feature.game.presentation.generated.resources.passport_hint_highlight_format
import crew.feature.game.presentation.generated.resources.passport_hint_prefix
import crew.feature.game.presentation.generated.resources.passport_hint_suffix
import crew.feature.game.presentation.generated.resources.passport_page_head
import crew.feature.game.presentation.generated.resources.passport_page_number
import crew.feature.game.presentation.generated.resources.passport_rare_host_emoji
import crew.feature.game.presentation.generated.resources.passport_rare_host_hint
import crew.feature.game.presentation.generated.resources.passport_rare_host_name
import crew.feature.game.presentation.generated.resources.passport_rare_section_title
import crew.feature.game.presentation.generated.resources.passport_share_action
import crew.feature.game.presentation.generated.resources.passport_subtitle
import crew.feature.game.presentation.generated.resources.passport_title_format
import crew.feature.game.presentation.generated.resources.passport_top_bar_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

/**
 * Damga Pasaportu — gecenin izi. Her yeni kişi pasaporta kendine özel bir damga
 * bırakır; puan tablosu değil, kimlerle oynadığının kaydı. Veri kaynağı:
 * GET /events/{eventId}/address-book. VM, eventId'yi route'un SavedStateHandle'ından okur.
 */
@Composable
fun PassportRoot(
    onBack: () -> Unit,
    onCollectStamp: () -> Unit,
    onSharePage: () -> Unit,
    viewModel: PassportViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PassportScreen(
        state = state,
        onBack = onBack,
        onCollectStamp = onCollectStamp,
        onSharePage = onSharePage,
    )
}

@Composable
fun PassportScreen(
    state: PassportState,
    onBack: () -> Unit,
    onCollectStamp: () -> Unit,
    onSharePage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Seçim salt görsel bir detay paneli açar; kalıcı olması gerekmediği için VM'e taşınmadı.
    var selectedStampId by rememberSaveable { mutableStateOf<String?>(null) }

    AppScaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                state = AppTopBarState.GameLobby(
                    stringResource(Res.string.passport_top_bar_title),
                ),
                onBackClick = onBack,
            )
        },
    ) { innerPadding ->
        if (state.isLoading) {
            FullScreenLoader(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
            )
            return@AppScaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            PassportHeader(stampCount = state.stamps.size)
            PassportPageCard(
                stamps = state.stamps,
                totalSlots = state.totalSlots,
                headerLeft = stringResource(Res.string.passport_page_head),
                headerRight = stringResource(Res.string.passport_page_number),
                selectedStampId = selectedStampId,
                onStampClick = { id ->
                    selectedStampId = if (selectedStampId == id) null else id
                },
            )
            state.stamps.firstOrNull { it.id == selectedStampId }?.let { stamp ->
                PassportStampDetailCard(
                    stamp = stamp,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
            if (state.emptySlotCount > 0) {
                MissingStampsHint(
                    missingCount = state.emptySlotCount,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }
            Text(
                text = stringResource(Res.string.passport_rare_section_title),
                style = MaterialTheme.typography.labelSmall.copy(color = AccessDefaults.TextMuted),
                modifier = Modifier.padding(top = 24.dp, bottom = 4.dp),
            )
            // Sunucuda nadir damga kataloğu yok; şimdilik tek nadir host 👑. Katalog
            // endpoint'i geldiğinde bu satır listeye dönüşür.
            PassportRareStampRow(
                emoji = stringResource(Res.string.passport_rare_host_emoji),
                name = stringResource(Res.string.passport_rare_host_name),
                hint = stringResource(Res.string.passport_rare_host_hint),
                isCollected = state.hostStampCollected,
            )
        }
    }
}

@Composable
private fun PassportHeader(
    stampCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.passport_title_format, stampCount, stampCount),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 7.dp),
        )
        Text(
            text = stringResource(Res.string.passport_subtitle),
            style = MaterialTheme.typography.bodySmall.copy(color = AccessDefaults.TextMuted),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .widthIn(max = 270.dp)
                .padding(top = 8.dp),
        )
    }
}

@Composable
private fun MissingStampsHint(
    missingCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .dottedBorder(
                color = AccessDefaults.Border,
                shape = RoundedCornerShape(14.dp),
                dotLength = 5.dp,
                gapLength = 5.dp,
            )
            .padding(horizontal = 14.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            painter = painterResource(AccessIcons.Sparkle),
            contentDescription = null,
            tint = AccessDefaults.Accent,
            modifier = Modifier.padding(top = 2.dp).size(15.dp),
        )
        Text(
            text = buildAnnotatedString {
                append(stringResource(Res.string.passport_hint_prefix))
                withStyle(
                    style = SpanStyle(
                        color = AccessDefaults.Accent,
                        fontWeight = FontWeight.Bold,
                    ),
                ) {
                    append(
                        stringResource(
                            Res.string.passport_hint_highlight_format,
                            missingCount,
                        ),
                    )
                }
                append(stringResource(Res.string.passport_hint_suffix))
            },
            style = MaterialTheme.typography.bodySmall.copy(color = AccessDefaults.TextSecondary),
        )
    }
}

private fun previewState() = PassportState(
    isLoading = false,
    totalSlots = 20,
    hostStampCollected = true,
    stamps = listOf(
        PassportStampUi("jana", "Jana V.", "J", AccessDefaults.Coral, StampShape.Round, -7f, "20:12", firstMatchWon = true, firstMatchTaskTitle = null),
        PassportStampUi("reka", "Réka B.", "R", AccessDefaults.Sky, StampShape.Square, 5f, "20:26", firstMatchWon = false, firstMatchTaskTitle = null),
        PassportStampUi("kuba", "Kuba D.", "K", AccessDefaults.Teal, StampShape.Notch, -4f, "20:41", firstMatchWon = false, firstMatchTaskTitle = "Three-person selfie"),
        PassportStampUi("adam", "Adam V.", "A", AccessDefaults.Amber, StampShape.Diamond, 3f, "21:02", firstMatchWon = true, firstMatchTaskTitle = null),
        PassportStampUi("tereza", "Tereza N.", "T", AccessDefaults.Mint, StampShape.Round, 8f, "21:15", firstMatchWon = true, firstMatchTaskTitle = null),
        PassportStampUi("lea", "Lea T.", "L", AccessDefaults.Rose, StampShape.Square, -6f, "21:29", firstMatchWon = false, firstMatchTaskTitle = "Best dance move"),
        PassportStampUi("ondra", "Ondra P.", "O", AccessDefaults.Accent, StampShape.Notch, 4f, "21:37", firstMatchWon = false, firstMatchTaskTitle = null),
        PassportStampUi("dario", "Dario", "👑", AccessDefaults.Amber, StampShape.Round, 6f, "21:48", firstMatchWon = true, firstMatchTaskTitle = null, isRare = true),
    ),
)

@Preview
@Composable
private fun PassportScreenPreview() {
    CrewTheme {
        PassportScreen(
            state = previewState(),
            onBack = {},
            onCollectStamp = {},
            onSharePage = {},
        )
    }
}
