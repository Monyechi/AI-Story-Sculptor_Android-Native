package com.monyechi.aistorysculptor.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.monyechi.aistorysculptor.ui.theme.DarkForestGreen

/**
 * Semi-transparent dark green container card that sits on top of the
 * background image. Matches the web app's `.dashboard-container` / `.module`
 * pattern (background-color: #2c3f2c; opacity: 0.95; border-radius: 8-15px).
 */
@Composable
fun DarkContainer(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .background(DarkForestGreen.copy(alpha = 0.95f))
            .padding(20.dp),
        content = content,
    )
}
