package org.luisito.admin360.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AdminColorScheme = lightColorScheme(
    primary = Color(0xFF4285F4),
    onPrimary = Color(0xFFFFFFFF),
    background = Color(0xFFE8F0FE),
    onBackground = Color(0xFF202124),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF202124),
    error = Color(0xFFC5221F),
    onError = Color(0xFFFFFFFF)
)

@Composable
fun Gestor360AdminTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AdminColorScheme,
        content = content
    )
}
