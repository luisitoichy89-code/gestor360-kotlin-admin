package org.luisito.admin360.ui.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Forma redondeada estándar para tarjetas/paneles neomórficos. */
val NeumorphicShape = RoundedCornerShape(20.dp)

/**
 * Modificador neomórfico: sombra suave y coloreada (efecto "hundido/elevado")
 * más un borde claro sutil que simula el reflejo de luz. Usa solo APIs
 * estables de Compose (Modifier.shadow + Modifier.border), sin Canvas nativo.
 */
fun Modifier.neumorphic(
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 8.dp,
    lightShadowColor: Color = LightShadow,
    darkShadowColor: Color = DarkShadow,
): Modifier {
    val shape = RoundedCornerShape(cornerRadius)
    return this
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = darkShadowColor,
            spotColor = darkShadowColor
        )
        .border(width = 1.dp, color = lightShadowColor.copy(alpha = 0.5f), shape = shape)
}

/** Variante para elementos que van sobre una superficie negra (surface = NeoBlack). */
fun Modifier.neumorphicOnBlack(
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 8.dp,
): Modifier = neumorphic(
    cornerRadius = cornerRadius,
    elevation = elevation,
    lightShadowColor = LightShadowOnBlack,
    darkShadowColor = DarkShadowOnBlack,
)

/** Variante para elementos que van sobre el fondo gris general de la app. */
fun Modifier.neumorphicOnBackground(
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 8.dp,
): Modifier = neumorphic(
    cornerRadius = cornerRadius,
    elevation = elevation,
    lightShadowColor = LightShadow,
    darkShadowColor = DarkShadow,
)
