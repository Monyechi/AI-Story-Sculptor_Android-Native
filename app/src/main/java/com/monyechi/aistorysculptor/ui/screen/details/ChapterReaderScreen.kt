package com.monyechi.aistorysculptor.ui.screen.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monyechi.aistorysculptor.ui.common.AppScaffold
import com.monyechi.aistorysculptor.ui.common.UiState
import com.monyechi.aistorysculptor.ui.theme.*
import com.monyechi.aistorysculptor.ui.viewmodel.ChapterReaderViewModel

@Composable
fun ChapterReaderScreen(
    bookId: Long,
    chapterId: Long,
    viewModel: ChapterReaderViewModel,
    onBack: () -> Unit,
) {
    val chapterState by viewModel.chapterState.collectAsStateWithLifecycle()

    LaunchedEffect(bookId, chapterId) {
        viewModel.load(bookId, chapterId)
    }

    AppScaffold(showOverlay = false) {
        when (val state = chapterState) {
            UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = AccentGreen)
                }
            }

            is UiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Chapter Reader", style = MaterialTheme.typography.headlineSmall, color = White)
                    Text(state.message, color = DangerRed)
                    OutlinedButton(
                        onClick = onBack,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = White),
                    ) {
                        Text("Back")
                    }
                }
            }

            is UiState.Success -> {
                val chapter = state.data
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    // ── Chapter title header (gradient green) ──
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(MediumForestGreen, DarkForestGreen)
                                    )
                                )
                                .padding(horizontal = 20.dp, vertical = 24.dp),
                        ) {
                            Column {
                                Text(
                                    text = "Chapter ${chapter.chapterNum}: ${chapter.title}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = WarmCream,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "${chapter.wordCount} words",
                                    color = Beige.copy(alpha = 0.7f),
                                    fontSize = 13.sp,
                                )
                            }
                        }
                    }

                    // ── Chapter content in a warm cream card ──
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = WarmCream),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                chapter.renderedContent.orEmpty().split("\n").forEach { paragraph ->
                                    if (paragraph.isNotBlank()) {
                                        Text(
                                            text = paragraph,
                                            color = DarkText,
                                            fontSize = 16.sp,
                                            lineHeight = 26.sp,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ── Back button ──
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            OutlinedButton(
                                onClick = onBack,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = White),
                            ) {
                                Text("Back to Book", fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}
