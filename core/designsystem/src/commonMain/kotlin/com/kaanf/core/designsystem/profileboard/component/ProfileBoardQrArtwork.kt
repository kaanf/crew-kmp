package com.kaanf.core.designsystem.profileboard.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import com.kaanf.core.designsystem.component.qr.UserQrCard
import com.kaanf.core.domain.model.profileboard.BoardQrRender
import com.kaanf.core.domain.model.profileboard.BoardQrRenderStatus
import com.kaanf.core.domain.model.profileboard.BoardQrTheme
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.profile_board_sample_qr
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** The scannable board QR, its server-rendered theme image and their loading/fallback states. */
@Composable
internal fun ProfileBoardQrArtwork(
    publicCode: String,
    qrStyleId: String,
    qrRender: BoardQrRender?,
    isEditing: Boolean,
    isSaving: Boolean,
    onQrImageError: () -> Unit,
    qrPadding: Dp,
    modifier: Modifier = Modifier,
) {
    val qrTheme = BoardQrTheme.find(qrStyleId)
    val render = qrRender?.takeIf { it.themeId == qrStyleId }
    val qrImageUrl = render?.takeIf { it.status == BoardQrRenderStatus.READY }?.imageUrl
    Box(modifier.fillMaxSize()) {
        val description = stringResource(Res.string.profile_board_sample_qr)
        var themedImageShown by remember(qrStyleId, qrImageUrl) { mutableStateOf(false) }
        var imageFailed by remember(qrStyleId, qrImageUrl) { mutableStateOf(false) }
        var retryCount by remember(qrStyleId, qrImageUrl) { mutableStateOf(0) }
        LaunchedEffect(qrStyleId, qrImageUrl, imageFailed) {
            if (imageFailed && retryCount == 0) {
                delay(1_000)
                retryCount = 1
                imageFailed = false
            }
        }
        val loadingQr = shouldShowQrLoading(qrStyleId, qrRender, isEditing, isSaving,
            themedImageShown, imageFailed)
        val previewing = isEditing && !isSaving && qrTheme != null
        // Failed themes/images fall back to the scannable regular QR, ending the loader.
        if (!loadingQr && !previewing && publicCode.isNotBlank() && !themedImageShown) UserQrCard(
            modifier = Modifier.padding(qrPadding),
            inputText = publicCode,
            styleId = qrStyleId,
            qrContentDescription = description,
        )
        if (qrImageUrl != null && !imageFailed) {
            key(qrStyleId, qrImageUrl, retryCount) {
                AsyncImage(
                    model = qrImageUrl,
                    contentDescription = if (themedImageShown) description else null,
                    onSuccess = { themedImageShown = true },
                    onError = {
                        imageFailed = true
                        if (retryCount > 0) onQrImageError()
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        } else if (previewing) Image(
            painter = painterResource(qrTheme.preview()),
            contentDescription = description,
            modifier = Modifier.fillMaxSize(),
        )
        if (loadingQr) ProfileBoardQrLoading(Modifier.matchParentSize())
    }
}
