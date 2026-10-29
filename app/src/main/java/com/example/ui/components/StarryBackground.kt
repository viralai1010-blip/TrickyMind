package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CosmicDarkBottom
import com.example.ui.theme.CosmicDarkMid
import com.example.ui.theme.CosmicDarkTop
import kotlin.random.Random

private data class Star(
    val xRatio: Float,
    val yRatio: Float,
    val radius: Float,
    val alpha: Float
)

@Composable
fun StarryBackground(modifier: Modifier = Modifier) {
    val stars = remember {
        val rand = Random(42)
        List(70) {
            Star(
                xRatio = rand.nextFloat(),
                yRatio = rand.nextFloat(),
                radius = rand.nextFloat() * 1.8f + 0.6f,
                alpha = rand.nextFloat() * 0.6f + 0.3f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Deep cosmic vertical gradient
        val backgroundBrush = Brush.verticalGradient(
            colors = listOf(
                CosmicDarkTop,
                CosmicDarkMid,
                CosmicDarkBottom
            ),
            startY = 0f,
            endY = height
        )
        drawRect(brush = backgroundBrush)

        // Radial subtle cosmic nebulae in upper and center areas
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x338B5CF6), Color.Transparent),
                center = Offset(width * 0.7f, height * 0.25f),
                radius = width * 0.7f
            ),
            radius = width * 0.7f,
            center = Offset(width * 0.7f, height * 0.25f)
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x2238BDF8), Color.Transparent),
                center = Offset(width * 0.2f, height * 0.65f),
                radius = width * 0.6f
            ),
            radius = width * 0.6f,
            center = Offset(width * 0.2f, height * 0.65f)
        )

        // Stars
        stars.forEach { star ->
            val center = Offset(star.xRatio * width, star.yRatio * height)
            drawCircle(
                color = Color.White.copy(alpha = star.alpha),
                radius = star.radius,
                center = center
            )
        }
    }
}
