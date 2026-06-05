package com.kaanf.core.designsystem.component.logo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.CrewTheme
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.crew_logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LogoCard(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(Res.drawable.crew_logo),
            contentDescription = "logo"
        )
    }
}

@Composable
@Preview
fun LogoCardPreview() {
    CrewTheme {
        LogoCard()
    }
}
