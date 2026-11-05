package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerPlaceholder(
    modifier: Modifier = Modifier,
    shapeRadius: Int = 12
) {
    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val baseColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    val highlightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)

    val brush = Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset(translateAnim - 200f, translateAnim - 200f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(shapeRadius.dp))
            .background(brush)
    )
}

@Composable
fun SearchResultSkeleton() {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ShimmerPlaceholder(modifier = Modifier.size(width = 160.dp, height = 20.dp))
                ShimmerPlaceholder(modifier = Modifier.size(width = 70.dp, height = 20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            ShimmerPlaceholder(modifier = Modifier.size(width = 220.dp, height = 14.dp))
            Spacer(modifier = Modifier.height(8.dp))
            ShimmerPlaceholder(modifier = Modifier.size(width = 140.dp, height = 14.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ShimmerPlaceholder(modifier = Modifier.size(width = 90.dp, height = 24.dp))
                ShimmerPlaceholder(modifier = Modifier.size(width = 110.dp, height = 24.dp))
            }
        }
    }
}
