package com.monyechi.aistorysculptor.ui.screen.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.monyechi.aistorysculptor.ui.common.AppScaffold
import com.monyechi.aistorysculptor.ui.common.DangerButton
import com.monyechi.aistorysculptor.ui.common.UiState
import com.monyechi.aistorysculptor.ui.theme.*
import com.monyechi.aistorysculptor.ui.viewmodel.BookDetailsViewModel
import java.io.File

@Composable
fun BookDetailsScreen(
    bookId: Long,
    viewModel: BookDetailsViewModel,
    onReadChapter: (Long) -> Unit,
    onBack: () -> Unit,
) {
    val detailsState by viewModel.detailsState.collectAsStateWithLifecycle()
    val isBusy by viewModel.isBusy.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()
    val renderProgress by viewModel.renderProgress.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(bookId) { viewModel.load(bookId) }

    LaunchedEffect(actionMessage) {
        actionMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    AppScaffold {
        Column(modifier = Modifier.fillMaxSize()) {
            SnackbarHost(hostState = snackbarHostState)

            when (val state = detailsState) {
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
                        Text("Book Details", style = MaterialTheme.typography.headlineSmall, color = White)
                        Text(state.message, color = DangerRed)
                        PrimaryButton("Retry", onClick = { viewModel.load(bookId) })
                        SecondaryButton("Back", onClick = onBack)
                    }
                }

                is UiState.Success -> {
                    val details = state.data
                    val book = details.book

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        // ── Book Header (gradient green card like .book-header) ──
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(MediumForestGreen, DarkForestGreen)
                                        )
                                    )
                                    .padding(20.dp),
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Text(
                                        book.title,
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = WarmCream,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        "by ${book.author}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Beige,
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "${book.bookType} • ${book.genre}",
                                        color = Beige.copy(alpha = 0.85f),
                                        fontSize = 14.sp,
                                    )
                                    Text(
                                        "Language: ${book.language} • POV: ${book.pov}",
                                        color = Beige.copy(alpha = 0.7f),
                                        fontSize = 13.sp,
                                    )

                                    book.coverArtPath?.let { path ->
                                        Spacer(Modifier.height(12.dp))
                                        AsyncImage(
                                            model = File(path),
                                            contentDescription = "Cover art",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(220.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                        )
                                    }

                                    if (book.summary.isNotBlank()) {
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            book.summary,
                                            color = WarmCream.copy(alpha = 0.9f),
                                            fontSize = 14.sp,
                                        )
                                    }
                                }
                            }
                        }

                        // ── AI Actions Card (matching content-card style) ──
                        item {
                            ContentCard {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        "AI Actions",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = DarkOlive,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        PrimaryButton(
                                            "Generate Summary",
                                            enabled = !isBusy,
                                            onClick = { viewModel.generateSummary(bookId) },
                                            modifier = Modifier.weight(1f),
                                        )
                                        AccentButton(
                                            "Cover Art",
                                            enabled = !isBusy,
                                            onClick = { viewModel.generateCoverArt(bookId) },
                                            modifier = Modifier.weight(1f),
                                        )
                                    }
                                }
                            }
                        }

                        // ── Render progress ──
                        renderProgress?.let { progress ->
                            item {
                                ContentCard {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        CircularProgressIndicator(
                                            color = AccentGreen,
                                            modifier = Modifier.height(20.dp),
                                        )
                                        Text(
                                            "${progress.message} (${progress.currentChapter}/${progress.totalChapters})",
                                            color = DarkOlive,
                                            fontSize = 14.sp,
                                        )
                                    }
                                }
                            }
                        }

                        // ── Chapters section ──
                        item {
                            ContentCard {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            "Chapters (${details.chapters.size})",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = DarkOlive,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        AccentButton(
                                            "+ Auto",
                                            enabled = !isBusy,
                                            onClick = { viewModel.autoGenerateChapters(bookId) },
                                        )
                                    }
                                    if (details.chapters.any { !it.rendered }) {
                                        PrimaryButton(
                                            "Render All Unrendered",
                                            enabled = !isBusy,
                                            onClick = { viewModel.renderAllChapters(bookId) },
                                            modifier = Modifier.fillMaxWidth(),
                                        )
                                    }
                                }
                            }
                        }

                        items(details.chapters) { chapter ->
                            ContentCard {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        "Ch. ${chapter.chapterNum}: ${chapter.title}",
                                        fontWeight = FontWeight.SemiBold,
                                        color = DarkOlive,
                                        fontSize = 16.sp,
                                    )
                                    Text(
                                        chapter.summary,
                                        color = GrayBody,
                                        fontSize = 13.sp,
                                        maxLines = 2,
                                    )
                                    // Status badge
                                    Text(
                                        if (chapter.rendered) "Rendered (${chapter.wordCount} words)" else "Not rendered",
                                        color = if (chapter.rendered) AccentGreen else WarningYellow,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp,
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        if (!chapter.rendered) {
                                            PrimaryButton(
                                                "Render",
                                                enabled = !isBusy,
                                                onClick = { viewModel.renderChapter(bookId, chapter.id) },
                                            )
                                        } else {
                                            AccentButton(
                                                "Read",
                                                onClick = { onReadChapter(chapter.id) },
                                            )
                                        }
                                        DangerButton(
                                            "Delete",
                                            onClick = { viewModel.deleteChapter(bookId, chapter.id) },
                                        )
                                    }
                                }
                            }
                        }

                        // ── Characters section ──
                        item {
                            ContentCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        "Characters (${details.characters.size})",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = DarkOlive,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    AccentButton(
                                        "+ Auto",
                                        enabled = !isBusy,
                                        onClick = { viewModel.autoGenerateCharacter(bookId) },
                                    )
                                }
                            }
                        }

                        items(details.characters) { character ->
                            ContentCard {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        character.name,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DarkOlive,
                                        fontSize = 16.sp,
                                    )
                                    Text(
                                        "${character.role} • Age: ${character.age ?: "?"}",
                                        color = GrayBody,
                                        fontSize = 13.sp,
                                    )
                                    character.bio?.let {
                                        Text(it, color = DarkText, fontSize = 13.sp, maxLines = 2)
                                    }
                                    DangerButton(
                                        "Delete",
                                        onClick = { viewModel.deleteCharacter(bookId, character.id) },
                                    )
                                }
                            }
                        }

                        // ── Back button ──
                        item {
                            Spacer(Modifier.height(8.dp))
                            SecondaryButton("Back to Library", onClick = onBack, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }
}

// ── Reusable styled components for this screen ──

@Composable
private fun ContentCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = WarmCream),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
private fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MediumForestGreen,
            contentColor = White,
            disabledContainerColor = MediumForestGreen.copy(alpha = 0.5f),
        ),
    ) {
        Text(text, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun AccentButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AccentGreen,
            contentColor = White,
            disabledContainerColor = AccentGreen.copy(alpha = 0.5f),
        ),
    ) {
        Text(text, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = White),
    ) {
        Text(text, fontWeight = FontWeight.Medium)
    }
}
