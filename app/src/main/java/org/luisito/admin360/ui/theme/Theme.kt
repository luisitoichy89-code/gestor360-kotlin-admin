package org.luisito.admin360.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SoftUiColorScheme = lightColorScheme(
    background = SoftGrayBackground,
    onBackground = PureWhite,
    surface = NeoBlack,
    onSurface = PureWhite,
    onSurfaceVariant = PureWhite,
    surfaceVariant = NeoBlack,
    primary = IconBlue,
    onPrimary = PureWhite,
    primaryContainer = NeoBlack,
    onPrimaryContainer = IconBlue,
    secondary = IconBlue,
    onSecondary = PureWhite,
    outline = LineOrange,
    outlineVariant = LineOrange,
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
