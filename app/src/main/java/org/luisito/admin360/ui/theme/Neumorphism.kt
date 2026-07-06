package org.luisito.admin360.ui.theme

import android.graphics.Paint as AndroidPaint
import android.os.Build
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.neumorphic(
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 8.dp,
    lightShadowColor: Color = LightShadow,
    darkShadowColor: Color = DarkShadow,
): Modifier = this
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawBehind {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val radiusPx = cornerRadius.toPx()
            val elevationPx = elevation.toPx()

            drawIntoCanvas { canvas ->
                val darkPaint = AndroidPaint().apply {
                    setShadowLayer(elevationPx, elevationPx / 2, elevationPx / 2, darkShadowColor.toArgb())
                }
                canvas.nativeCanvas.drawRoundRect(0f, 0f, size.width, size.height, radiusPx, radiusPx, darkPaint)

                val lightPaint = AndroidPaint().apply {
                    setShadowLayer(elevationPx, -elevationPx / 2, -elevationPx / 2, lightShadowColor.toArgb())
                }
                canvas.nativeCanvas.drawRoundRect(0f, 0f, size.width, size.height, radiusPx, radiusPx, lightPaint)
            }
        }
    }

fun Modifier.neumorphicOnBlack(
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 8.dp,
): Modifier = neumorphic(
    cornerRadius = cornerRadius,
    elevation = elevation,
    lightShadowColor = LightShadowOnBlack,
    darkShadowColor = DarkShadowOnBlack,
)

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
