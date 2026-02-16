package com.monyechi.aistorysculptor.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monyechi.aistorysculptor.ui.theme.*

/**
 * Primary action button matching the web app's `.banner-btn` style:
 * olive-yellow background (#ABAD60), white text, rounded corners.
 */
@Composable
fun BannerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = YellowGreen,
            contentColor = White,
            disabledContainerColor = YellowGreen.copy(alpha = 0.5f),
        ),
        contentPadding = PaddingValues(horizontal = 30.dp, vertical = 10.dp),
    ) {
        Text(
            text = text,
            fontFamily = RocaFont,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
        )
    }
}

/**
 * Green success / "Add Book" style button matching `.add-book-btn`:
 * #4CAF50 background, white text.
 */
@Composable
fun GreenButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = enabled,
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SuccessGreen,
            contentColor = White,
            disabledContainerColor = SuccessGreen.copy(alpha = 0.5f),
        ),
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

/**
 * Coral/salmon submit button matching `.submitBtn` on create book:
 * #e0746c background, white text.
 */
@Composable
fun CoralButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = enabled,
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Coral,
            contentColor = White,
            disabledContainerColor = Coral.copy(alpha = 0.5f),
        ),
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

/**
 * Danger / cancel button matching the web app's red cancel buttons:
 * #ff6b6b background, white text.
 */
@Composable
fun DangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DangerRed,
            contentColor = White,
        ),
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

/**
 * Olive link-style button matching `.login-modal-submit` (#474321).
 */
@Composable
fun OliveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DarkOlive,
            contentColor = White,
            disabledContainerColor = DarkOlive.copy(alpha = 0.5f),
        ),
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/**
 * Outlined secondary button using the forest green outline.
 */
@Composable
fun ForestOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = White,
        ),
    ) {
        Text(text = text, fontWeight = FontWeight.Medium)
    }
}
