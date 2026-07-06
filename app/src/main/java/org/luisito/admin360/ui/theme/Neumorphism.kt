package org.luisito.admin360.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asFrameworkPaint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Modificador neomórfico genérico: dibuja una sombra oscura (abajo-derecha)
 * y una sombra clara (arriba-izquierda) para simular relieve suave.
 * Usar sobre un fondo del MISMO color que [lightShadowColor]/[darkShadowColor] estén calibradas
 * (fondo gris -> LightShadow/DarkShadow; superficie negra -> LightShadowOnBlack/DarkShadowOnBlack).
 */
fun Modifier.neumorphic(
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 8.dp,
    lightShadowColor: Color = LightShadow,
    darkShadowColor: Color = DarkShadow,
): Modifier = this
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawBehind {
        val radiusPx = cornerRadius.toPx()
        val elevationPx = elevation.toPx()

        drawIntoCanvas { canvas ->
            val darkPaint = Paint().asFrameworkPaint().apply {
                color = android.graphics.Color.TRANSPARENT
                setShadowLayer(elevationPx, elevationPx / 2, elevationPx / 2, darkShadowColor.toArgb())
            }
            canvas.nativeCanvas.drawRoundRect(
                0f, 0f, size.width, size.height, radiusPx, radiusPx, darkPaint
            )

            val lightPaint = Paint().asFrameworkPaint().apply {
                color = android.graphics.Color.TRANSPARENT
                setShadowLayer(elevationPx, -elevationPx / 2, -elevationPx / 2, lightShadowColor.toArgb())
            }
            canvas.nativeCanvas.drawRoundRect(
                0f, 0f, size.width, size.height, radiusPx, radiusPx, lightPaint
            )
        }
    }

/** Variante lista para usar sobre tarjetas/superficies negras (surface = NeoBlack). */
fun Modifier.neumorphicOnBlack(
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 8.dp,
): Modifier = neumorphic(
    cornerRadius = cornerRadius,
    elevation = elevation,
    lightShadowColor = LightShadowOnBlack,
    darkShadowColor = DarkShadowOnBlack,
)

/** Variante lista para usar sobre el fondo gris general de la app. */
fun Modifier.neumorphicOnBackground(
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 8.dp,
): Modifier = neumorphic(
    cornerRadius = cornerRadius,
    elevation = elevation,
    lightShadowColor = LightShadow,
    darkShadowColor = DarkShadow,
)

val NeumorphicShape = RoundedCornerShape(20.dp)
