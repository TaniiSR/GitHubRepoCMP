package com.task.githubrepo.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

private const val SkeletonRows = 12

@Composable
internal fun LoadingView(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val shift by transition.animateFloat(
        initialValue = -600f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmerShift",
    )
    val brush = Brush.linearGradient(
        colors = listOf(Grey, Color(0xFFF6F6F6), Grey),
        start = Offset(shift, 0f),
        end = Offset(shift + 400f, 200f),
    )
    Column(modifier.fillMaxSize().testTag("loadingView")) {
        repeat(SkeletonRows) {
            Row(
                Modifier.fillMaxWidth().padding(start = 25.dp, top = 25.dp, bottom = 25.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(40.dp).clip(CircleShape).background(brush))
                Spacer(Modifier.width(25.dp))
                Column {
                    Box(Modifier.width(80.dp).height(10.dp).background(brush))
                    Spacer(Modifier.height(8.dp))
                    Box(Modifier.width(180.dp).height(14.dp).background(brush))
                }
            }
            Box(Modifier.fillMaxWidth().height(0.5.dp).background(Grey))
        }
    }
}
