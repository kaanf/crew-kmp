package com.kaanf.auth.presentation.signinmethods

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.auth.domain.model.LinkedIdentity
import com.kaanf.auth.domain.model.SignInMethods
import com.kaanf.auth.domain.model.SocialProvider
import com.kaanf.auth.presentation.signinmethods.component.IdentityConflictBottomSheet
import com.kaanf.auth.presentation.signinmethods.component.PasswordBottomSheet
import com.kaanf.auth.presentation.signinmethods.component.SignInMethodAction
import com.kaanf.auth.presentation.signinmethods.component.SignInMethodRow
import com.kaanf.auth.presentation.social.rememberSocialSignInLauncher
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.sheet.TwoOptionBottomSheet
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import crew.feature.auth.presentation.generated.resources.Res
import crew.feature.auth.presentation.generated.resources.sign_in_methods_change_password
import crew.feature.auth.presentation.generated.resources.sign_in_methods_count
import crew.feature.auth.presentation.generated.resources.sign_in_methods_description
import crew.feature.auth.presentation.generated.resources.sign_in_methods_email
import crew.feature.auth.presentation.generated.resources.sign_in_methods_footer
import crew.feature.auth.presentation.generated.resources.sign_in_methods_headline
import crew.feature.auth.presentation.generated.resources.sign_in_methods_last_warning
import crew.feature.auth.presentation.generated.resources.sign_in_methods_no_password
import crew.feature.auth.presentation.generated.resources.sign_in_methods_not_connected
import crew.feature.auth.presentation.generated.resources.sign_in_methods_section
import crew.feature.auth.presentation.generated.resources.sign_in_methods_unlink_cancel
import crew.feature.auth.presentation.generated.resources.sign_in_methods_unlink_confirm
import crew.feature.auth.presentation.generated.resources.sign_in_methods_unlink_description
import crew.feature.auth.presentation.generated.resources.sign_in_methods_unlink_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignInMethodsRoot(
    onBack: () -> Unit,
    viewModel: SignInMethodsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val launcher =
        rememberSocialSignInLauncher { result ->
            viewModel.onAction(SignInMethodsAction.OnSignInResult(result))
        }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is SignInMethodsEvent.LaunchProvider -> launcher.launch(event.provider)
        }
    }

    SignInMethodsScreen(
        state = state,
        onBack = onBack,
        onAction = viewModel::onAction,
    )
}

@Composable
fun SignInMethodsScreen(
    state: SignInMethodsState,
    onBack: () -> Unit,
    onAction: (SignInMethodsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                state = AppTopBarState.SignInMethods,
                onBackClick = onBack,
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            val methods = state.methods

            when {
                methods != null -> MethodsContent(
                    methods = methods,
                    busyProvider = state.busyProvider,
                    onAction = onAction,
                )

                state.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center).size(28.dp),
                    color = AccessDefaults.Accent,
                    strokeWidth = 2.dp,
                )
            }
        }
    }

    val unlinkTarget = state.unlinkTarget
    if (unlinkTarget != null) {
        TwoOptionBottomSheet(
            title = stringResource(Res.string.sign_in_methods_unlink_title, unlinkTarget.name),
            description = stringResource(
                Res.string.sign_in_methods_unlink_description,
                unlinkTarget.name,
            ),
            confirmButtonText = stringResource(
                Res.string.sign_in_methods_unlink_confirm,
                unlinkTarget.name,
            ),
            cancelButtonText = stringResource(Res.string.sign_in_methods_unlink_cancel),
            onConfirmClicked = { onAction(SignInMethodsAction.OnUnlinkConfirm) },
            onCancelClicked = { onAction(SignInMethodsAction.OnUnlinkDismiss) },
            onDismiss = { onAction(SignInMethodsAction.OnUnlinkDismiss) },
            isDismissable = true,
        )
    }

    val conflictProvider = state.conflictProvider
    if (conflictProvider != null) {
        IdentityConflictBottomSheet(
            providerName = conflictProvider.name,
            onDismiss = { onAction(SignInMethodsAction.OnConflictDismiss) },
        )
    }

    val passwordSheet = state.passwordSheet
    if (passwordSheet != null) {
        PasswordBottomSheet(
            mode = passwordSheet,
            accountEmail = state.methods?.accountEmail.orEmpty(),
            isSaving = state.isSavingPassword,
            onDismiss = { onAction(SignInMethodsAction.OnPasswordSheetDismiss) },
            onSubmit = { current, new ->
                onAction(SignInMethodsAction.OnPasswordSubmit(current, new))
            },
        )
    }
}

@Composable
private fun MethodsContent(
    methods: SignInMethods,
    busyProvider: SocialProvider?,
    onAction: (SignInMethodsAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp),
    ) {
        Text(
            text = stringResource(Res.string.sign_in_methods_headline),
            style = MaterialTheme.typography.headlineMedium.copy(color = AccessDefaults.TextPrimary),
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(Res.string.sign_in_methods_description),
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextMuted,
                fontSize = 13.sp,
            ),
        )

        Spacer(modifier = Modifier.height(22.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.sign_in_methods_section),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = AccessDefaults.TextMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 1.2.sp,
                ),
            )

            Text(
                text = stringResource(Res.string.sign_in_methods_count, methods.linkedCount),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = AccessDefaults.TextFaint,
                    fontSize = 10.sp,
                    letterSpacing = 1.2.sp,
                ),
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SocialProvider.entries.forEach { provider ->
                ProviderRow(
                    provider = provider,
                    methods = methods,
                    isBusy = busyProvider == provider,
                    onAction = onAction,
                )
            }

            EmailRow(methods = methods, onAction = onAction)
        }

        if (methods.linkedCount == 1) {
            Spacer(modifier = Modifier.height(12.dp))
            LastMethodWarning(methods = methods)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(Res.string.sign_in_methods_footer),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextFaint,
                fontSize = 11.5.sp,
            ),
        )
    }
}

@Composable
private fun ProviderRow(
    provider: SocialProvider,
    methods: SignInMethods,
    isBusy: Boolean,
    onAction: (SignInMethodsAction) -> Unit,
) {
    val identity = methods.identityOf(provider)
    val isLinked = identity != null
    val notConnected = stringResource(Res.string.sign_in_methods_not_connected)

    SignInMethodRow(
        icon = provider.icon,
        // Google "G" çok renkli; olduğu gibi çizilir. Apple glifi içerik rengiyle boyanır.
        tintIcon = provider != SocialProvider.Google,
        iconBackground = when (provider) {
            SocialProvider.Apple -> AppleGlyphBackground
            SocialProvider.Google -> AccessDefaults.SurfaceHigh
        },
        iconTint = when (provider) {
            SocialProvider.Apple -> AppleGlyphTint
            SocialProvider.Google -> AccessDefaults.TextPrimary
        },
        // Marka adları çevrilmez; enum adı zaten "Google" / "Apple".
        name = provider.name,
        isSignUpMethod = isLinked && methods.signUpProvider == provider,
        // Eski kayıtlarda sağlayıcı e-postası boş olabilir; hesabın kendi e-postasına düşer.
        subtitle = identity?.email ?: if (isLinked) methods.accountEmail else notConnected,
        isLinked = isLinked,
        action = when {
            isBusy -> SignInMethodAction.Busy
            !isLinked -> SignInMethodAction.Connect {
                onAction(SignInMethodsAction.OnConnectClick(provider))
            }

            methods.canUnlink(provider) -> SignInMethodAction.Unlink {
                onAction(SignInMethodsAction.OnUnlinkClick(provider))
            }

            else -> SignInMethodAction.Locked
        },
    )
}

/**
 * E-posta satırının aksiyonu her zaman şifre sheet'i: şifre yoksa belirlemek, varsa
 * değiştirmek için. Şifreyi kaldırma diye bir akış yok — hesabın e-postası kayıt anında
 * sabitlenir ve şifre yalnız ikinci bir giriş yolu ekler.
 */
@Composable
private fun EmailRow(
    methods: SignInMethods,
    onAction: (SignInMethodsAction) -> Unit,
) {
    SignInMethodRow(
        icon = AccessIcons.Mail,
        name = stringResource(Res.string.sign_in_methods_email),
        subtitle = if (methods.hasPassword) {
            methods.accountEmail
        } else {
            stringResource(Res.string.sign_in_methods_no_password)
        },
        isLinked = methods.hasPassword,
        iconTint = AccessDefaults.TextSecondary,
        action = when {
            !methods.hasPassword -> SignInMethodAction.Connect {
                onAction(SignInMethodsAction.OnPasswordClick)
            }

            // Şifre tek giriş yoluysa bunu açıkça söyle; değilse sağ taraf boş kalır.
            methods.linkedCount == 1 -> SignInMethodAction.Locked
            else -> SignInMethodAction.None
        },
        footerLabel = stringResource(Res.string.sign_in_methods_change_password)
            .takeIf { methods.hasPassword },
        onFooterClick = { onAction(SignInMethodsAction.OnPasswordClick) },
    )
}

@Composable
private fun LastMethodWarning(methods: SignInMethods) {
    val onlyMethod = methods.identities.firstOrNull()?.provider?.name
        ?: stringResource(Res.string.sign_in_methods_email)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AccessDefaults.Warning.copy(alpha = 0.09f), RoundedCornerShape(13.dp))
            .border(
                width = 1.dp,
                color = AccessDefaults.Warning.copy(alpha = 0.26f),
                shape = RoundedCornerShape(13.dp),
            )
            .padding(horizontal = 13.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            modifier = Modifier.size(14.dp).padding(top = 1.dp),
            painter = painterResource(AccessIcons.Info),
            tint = AccessDefaults.Warning,
            contentDescription = null,
        )

        Text(
            text = stringResource(Res.string.sign_in_methods_last_warning, onlyMethod),
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextSecondary,
                fontSize = 12.sp,
            ),
        )
    }
}

// Apple marka kuralı: glif koyu zemin üzerinde beyaz durur.
private val AppleGlyphBackground = Color(0xFF0D0B09)
private val AppleGlyphTint = Color.White

private val SocialProvider.icon
    get() = when (this) {
        SocialProvider.Google -> AccessIcons.GoogleG
        SocialProvider.Apple -> AccessIcons.AppleLogo
    }

@Composable
@Preview
private fun SignInMethodsScreenPreview() {
    CrewTheme {
        SignInMethodsScreen(
            state = SignInMethodsState(
                isLoading = false,
                methods = SignInMethods(
                    accountEmail = "kaan@crew.app",
                    hasPassword = false,
                    signUpProvider = SocialProvider.Google,
                    identities = listOf(
                        LinkedIdentity(provider = SocialProvider.Google, email = "kaan@gmail.com"),
                    ),
                ),
            ),
            onBack = {},
            onAction = {},
        )
    }
}
