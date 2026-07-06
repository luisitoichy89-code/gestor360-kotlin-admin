package org.luisito.admin360.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AzulOscuro = Color(0xFF0B0F14)
private val GrisCard = Color(0xFF111827)
private val AzulAcento = Color(0xFF4DA3FF)
private val Blanco = Color(0xFFFFFFFF)
private val GrisClaro = Color(0xFFEAF0FF)
private val GrisMedio = Color(0xFF9FB3C8)
private val RojoError = Color(0xFFEF4444)

private val GestorDarkColorScheme = darkColorScheme(
    primary = AzulAcento,
    onPrimary = Blanco,
    background = AzulOscuro,
    onBackground = GrisClaro,
    surface = GrisCard,
    onSurface = GrisClaro,
    onSurfaceVariant = GrisMedio,
    error = RojoError,
    onError = Blanco,
    primaryContainer = GrisCard,
    onPrimaryContainer = AzulAcento
)

@Composable
fun Gestor360Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GestorDarkColorScheme,
        content = content
    )
}
