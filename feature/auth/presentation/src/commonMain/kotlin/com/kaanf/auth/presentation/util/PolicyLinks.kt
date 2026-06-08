package com.kaanf.auth.presentation.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.withLink
import com.kaanf.core.designsystem.theme.AccessDefaults

object PolicyUrls {
    const val TERMS_OF_USE = "https://kaanf.github.io/crew-policy/terms-of-use"
    const val HOUSE_RULES = "https://kaanf.github.io/crew-policy/house-rules"
    const val PRIVACY_POLICY = "https://kaanf.github.io/crew-policy/privacy-policy"
}

fun AnnotatedString.Builder.appendPolicyLink(text: String, url: String) {
    withLink(
        LinkAnnotation.Url(
            url = url,
            styles = TextLinkStyles(style = SpanStyle(color = AccessDefaults.Accent)),
        ),
    ) {
        append(text)
    }
}
