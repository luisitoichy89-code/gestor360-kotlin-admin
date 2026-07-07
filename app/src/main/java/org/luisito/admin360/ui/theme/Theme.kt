package org.luisito.admin360.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SoftUiColorScheme = lightColorScheme(
    background = SoftGrayBackground,
    onBackground = PureWhite,

    surface = NeoBlack,
    onSurface = PureWhite,
    surfaceVariant = NeoBlack,
    onSurfaceVariant = PureWhite.copy(alpha = 0.7f),

    surfaceContainer = NeoBlack,
    surfaceContainerLow = NeoBlack,
    surfaceContainerLowest = NeoBlack,
    surfaceContainerHigh = LightShadowOnBlack,
    surfaceContainerHighest = LightShadowOnBlack,
    surfaceBright = LightShadowOnBlack,
    surfaceDim = NeoBlack,
    surfaceTint = IconBlue,
    inverseSurface = PureWhite,
    inverseOnSurface = NeoBlack,

    primary = IconBlue,
    onPrimary = PureWhite,
    primaryContainer = NeoBlack,
    onPrimaryContainer = IconBlue,
    secondary = IconBlue,
    onSecondary = PureWhite,
    outline = LineOrange,
    outlineVariant = LineOrange.copy(alpha = 0.5f),
    error = ErrorRed,
    onError = PureWhite
)

@Composable
fun Gestor360Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SoftUiColorScheme,
        content = content
    )
}
