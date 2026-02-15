package com.monyechi.aistorysculptor.ui.screen.details

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monyechi.aistorysculptor.ui.common.UiState
import com.monyechi.aistorysculptor.ui.viewmodel.BookDetailsViewModel

@Composable
fun BookDetailsScreen(
    bookId: Long,
    viewModel: BookDetailsViewModel,
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

    Column(modifier = Modifier.fillMaxSize()) {
        SnackbarHost(hostState = snackbarHostState)

        when (val state = detailsState) {
            UiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Book Details", style = MaterialTheme.typography.headlineSmall)
                    CircularProgressIndicator()
                }
            }

            is UiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Book Details", style = MaterialTheme.typography.headlineSmall)
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.load(bookId) }, modifier = Modifier.fillMaxWidth()) { Text("Retry") }
                    Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
                }
            }

            is UiState.Success -> {
                val details = state.data
                val book = details.book

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ── Book Info ─────────────────────────────────────
                    item {
                        Text(book.title, style = MaterialTheme.typography.headlineSmall)
                        Text("by ${book.author}", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(4.dp))
                        Text("${book.bookType} • ${book.genre}", style = MaterialTheme.typography.bodyMedium)
                        Text("Language: ${book.language} • POV: ${book.pov}", style = MaterialTheme.typography.bodySmall)
                        if (book.summary.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text(book.summary, style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    // ── AI Actions ────────────────────────────────────
                    item {
                        HorizontalDivider()
                        Text("AI Actions", style = MaterialTheme.typography.titleMedium)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                enabled = !isBusy,
                                onClick = { viewModel.generateSummary(bookId) },
                            ) { Text("Generate Summary") }
                            Button(
                                enabled = !isBusy,
                                onClick = { viewModel.generateCoverArt(bookId) },
                            ) { Text("Cover Art") }
                        }
                    }

                    // ── Render progress ───────────────────────────────
                    renderProgress?.let { progress ->
                        item {
                            Text(
                                "${progress.message} (${progress.currentChapter}/${progress.totalChapters})",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }

                    // ── Chapters ──────────────────────────────────────
                    item {
                        HorizontalDivider()
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Chapters (${details.chapters.size})", style = MaterialTheme.typography.titleMedium)
                            Button(
                                enabled = !isBusy,
                                onClick = { viewModel.autoGenerateChapters(bookId) },
                            ) { Text("+ Auto Chapter") }
                        }
                        if (details.chapters.any { !it.rendered }) {
                            Button(
                                enabled = !isBusy,
                                onClick = { viewModel.renderAllChapters(bookId) },
                                modifier = Modifier.fillMaxWidth(),
                            ) { Text("Render All Unrendered") }
                        }
                    }

                    items(details.chapters) { chapter ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    "Ch. ${chapter.chapterNum}: ${chapter.title}",
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(chapter.summary, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                                Text(
                                    if (chapter.rendered) "Rendered (${chapter.wordCount} words)" else "Not rendered",
                                    style = MaterialTheme.typography.labelSmall,
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (!chapter.rendered) {
                                        OutlinedButton(
                                            enabled = !isBusy,
                                            onClick = { viewModel.renderChapter(bookId, chapter.id) },
                                        ) { Text("Render") }
                                    }
                                    OutlinedButton(onClick = { viewModel.deleteChapter(bookId, chapter.id) }) { Text("Delete") }
                                }
                            }
                        }
                    }

                    // ── Characters ────────────────────────────────────
                    item {
                        HorizontalDivider()
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Characters (${details.characters.size})", style = MaterialTheme.typography.titleMedium)
                            Button(
                                enabled = !isBusy,
                                onClick = { viewModel.autoGenerateCharacter(bookId) },
                            ) { Text("+ Auto Character") }
                        }
                    }

                    items(details.characters) { character ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(character.name, fontWeight = FontWeight.SemiBold)
                                Text("${character.role} • Age: ${character.age ?: "?"}", style = MaterialTheme.typography.bodySmall)
                                character.bio?.let { Text(it, style = MaterialTheme.typography.bodySmall, maxLines = 2) }
                                OutlinedButton(onClick = { viewModel.deleteCharacter(bookId, character.id) }) { Text("Delete") }
                            }
                        }
                    }

                    // ── Navigation ────────────────────────────────────
                    item {
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
                    }
                }
            }
        }
    }
}
