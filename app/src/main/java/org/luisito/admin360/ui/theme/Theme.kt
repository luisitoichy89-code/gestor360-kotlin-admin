package org.luisito.admin360.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val Admin360DarkColorScheme = darkColorScheme(
    primary = WhiteText,
    onPrimary = BlackPrimary,
    background = BlackPrimary,
    onBackground = WhiteText,
    surface = BlackPrimary,
    onSurface = WhiteText,
    outline = GrayBorder,
    error = ErrorRed
)

@Composable
fun Gestor360Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Admin360DarkColorScheme,
        content = content
    )
}
