package com.sayon.ai.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sayon.ai.ui.screens.SayonGlowingLogo
import kotlinx.coroutines.delay

/**
 * Animated Dynamic Watermark for generated videos.
 * Position changes every 3 seconds:
 * 1. Bottom-Left (0-3s)
 * 2. Top-Left (3-6s)
 * 3. Top-Right (6-9s)
 * 4. Bottom-Left (9-12s)
 * 5. Top-Left (12-15s)
 * 6. Top-Right (15-18s)
 */
@Composable
fun VideoWatermarkOverlay(
    modifier: Modifier = Modifier
) {
    val positions = remember {
        listOf(
            Alignment.BottomStart, // أسفل اليسار (3s)
            Alignment.TopStart,    // أعلى اليسار (3s)
            Alignment.TopEnd,      // أعلى اليمين (3s)
            Alignment.BottomStart  // أسفل اليسار (3s)
        )
    }

    var positionIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000L) // Switch position every 3 seconds
            positionIndex = (positionIndex + 1) % positions.size
        }
    }

    val currentAlignment = positions[positionIndex]

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {
        AnimatedContent(
            targetState = currentAlignment,
            transitionSpec = {
                fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
            },
            modifier = Modifier.align(currentAlignment),
            label = "WatermarkPosition"
        ) { alignment ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .alpha(0.52f) // Semi-transparent
                    .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                SayonGlowingLogo(modifier = Modifier.size(42.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Sayon AI",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Generative Model",
                        color = Color(0xFF00E5FF),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
