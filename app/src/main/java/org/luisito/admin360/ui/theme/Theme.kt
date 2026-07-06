package org.luisito.admin360.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SoftUiColorScheme = lightColorScheme(
    background = SoftGrayBackground,
    surface = DarkSurface,
    onSurface = PureWhite,
    onBackground = DarkSurface,
    onSurfaceVariant = Color(0xFF9FB3C8),
    primary = Color(0xFF4DA3FF),
    onPrimary = PureWhite,
    primaryContainer = DarkSurface,
    onPrimaryContainer = Color(0xFF4DA3FF),
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
