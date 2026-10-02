package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BallWhiteText
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.PowerballRed
import com.example.ui.theme.PowerballRedGlow

enum class BallType {
    WHITE,
    POWERBALL,
    GOLD,
    CYAN
}

@Composable
fun LotteryBall(
    number: Int,
    type: BallType = BallType.WHITE,
    size: Dp = 44.dp,
    animateEntrance: Boolean = false,
    delayMs: Int = 0,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(if (animateEntrance) 0f else 1f) }

    LaunchedEffect(number, animateEntrance) {
        if (animateEntrance) {
            scale.snapTo(0f)
            kotlinx.coroutines.delay(delayMs.toLong())
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            )
        }
    }

    val (brush, textColor, borderColor) = when (type) {
        BallType.WHITE -> Triple(
            Brush.radialGradient(
                colors = listOf(Color(0xFFFFFFFF), Color(0xFFE2E8F0), Color(0xFFCBD5E1)),
                radius = 70f
            ),
            BallWhiteText,
            Color(0xFF94A3B8)
        )
        BallType.POWERBALL -> Triple(
            Brush.radialGradient(
                colors = listOf(PowerballRedGlow, PowerballRed, Color(0xFF9B111E)),
                radius = 75f
            ),
            Color.White,
            Color(0xFFFF858F)
        )
        BallType.GOLD -> Triple(
            Brush.radialGradient(
                colors = listOf(Color(0xFFFFEA80), CelestialGold, Color(0xFFB37E00)),
                radius = 75f
            ),
            Color(0xFF332000),
            Color(0xFFFFEB99)
        )
        BallType.CYAN -> Triple(
            Brush.radialGradient(
                colors = listOf(Color(0xFF90E0EF), Color(0xFF0077B6), Color(0xFF023E8A)),
                radius = 75f
            ),
            Color.White,
            Color(0xFFADE8F4)
        )
    }

    val fontSize = when {
        size >= 50.dp -> 18.sp
        size >= 40.dp -> 15.sp
        else -> 12.sp
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .scale(scale.value)
            .size(size)
            .shadow(elevation = 6.dp, shape = CircleShape, ambientColor = borderColor)
            .clip(CircleShape)
            .background(brush)
            .border(width = 1.5.dp, color = borderColor.copy(alpha = 0.8f), shape = CircleShape)
    ) {
        Text(
            text = if (number > 0) number.toString() else "-",
            color = textColor,
            fontSize = fontSize,
            fontWeight = FontWeight.Black
        )
    }
}
