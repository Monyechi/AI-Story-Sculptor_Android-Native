package com.monyechi.aistorysculptor.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.monyechi.aistorysculptor.R
import com.monyechi.aistorysculptor.ui.theme.OverlayDark

/**
 * Full-screen background image scaffold matching the web app's
 * background-image + dark overlay pattern used on every page.
 */
@Composable
fun AppScaffold(
    @DrawableRes backgroundRes: Int = R.drawable.mainbg,
    showOverlay: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image — covers entire screen
        Image(
            painter = painterResource(backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        // Semi-transparent overlay
        if (showOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(OverlayDark)
            )
        }
        // Content on top
        content()
    }
}
