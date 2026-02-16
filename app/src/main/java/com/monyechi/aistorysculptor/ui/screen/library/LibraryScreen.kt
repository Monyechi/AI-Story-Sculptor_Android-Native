package com.monyechi.aistorysculptor.ui.screen.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.monyechi.aistorysculptor.domain.model.Book
import com.monyechi.aistorysculptor.ui.common.AppScaffold
import com.monyechi.aistorysculptor.ui.common.DarkContainer
import com.monyechi.aistorysculptor.ui.common.GreenButton
import com.monyechi.aistorysculptor.ui.common.UiState
import com.monyechi.aistorysculptor.ui.theme.*
import com.monyechi.aistorysculptor.ui.viewmodel.LibraryViewModel
import java.io.File

@Composable
fun LibraryScreen(
    paddingValues: PaddingValues,
    viewModel: LibraryViewModel,
    onBookClick: (Long) -> Unit,
    onCreateClick: () -> Unit,
    onLogout: () -> Unit,
) {
    val booksState by viewModel.booksState.collectAsStateWithLifecycle()

    AppScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            // ── Dark green dashboard container ──
            DarkContainer(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 8.dp,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // ── Header row with title & logout ──
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "My Bookshelf",
                            style = MaterialTheme.typography.headlineSmall,
                            color = White,
                            fontWeight = FontWeight.Bold,
                        )
                        IconButton(onClick = onLogout) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Logout",
                                tint = Beige,
                            )
                        }
                    }

                    // ── Add Book button (green, matching .add-book-btn) ──
                    GreenButton(
                        text = "Add Book",
                        onClick = onCreateClick,
                    )

                    // ── Content area ──
                    when (val state = booksState) {
                        UiState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(color = AccentGreen)
                            }
                        }

                        is UiState.Error -> {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(state.message, color = DangerRed, style = MaterialTheme.typography.bodyMedium)
                                GreenButton(text = "Retry", onClick = { viewModel.loadBooks() })
                            }
                        }

                        is UiState.Success -> {
                            if (state.data.isEmpty()) {
                                Text(
                                    text = "No books yet.\nTap \"Add Book\" to create your first story!",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    color = Beige,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                )
                            } else {
                                // ── Beige scrollable book list (matching .scrollable-div) ──
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f, fill = false)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(BeigeAlpha90)
                                        .padding(8.dp),
                                ) {
                                    // ── Table header row ──
                                    Column {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(MediumForestGreen)
                                                .padding(horizontal = 8.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text("#", color = White, fontWeight = FontWeight.Bold, modifier = Modifier.width(30.dp), fontSize = 14.sp)
                                            Text("Cover", color = White, fontWeight = FontWeight.Bold, modifier = Modifier.width(60.dp), fontSize = 14.sp)
                                            Text("Book Title", color = White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 14.sp)
                                            Text("Action", color = White, fontWeight = FontWeight.Bold, modifier = Modifier.width(50.dp), fontSize = 14.sp, textAlign = TextAlign.Center)
                                        }

                                        LazyColumn(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(0.dp),
                                        ) {
                                            itemsIndexed(state.data) { index, book ->
                                                LibraryBookRow(
                                                    index = index + 1,
                                                    book = book,
                                                    onClick = { onBookClick(book.id) },
                                                )
                                                if (index < state.data.lastIndex) {
                                                    HorizontalDivider(color = DarkOlive.copy(alpha = 0.3f))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryBookRow(index: Int, book: Book, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Row number
        Text(
            text = "$index",
            color = DarkOlive,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(30.dp),
            fontSize = 14.sp,
        )

        // Cover art thumbnail
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MediumForestGreen.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            if (book.coverArtPath != null) {
                AsyncImage(
                    model = File(book.coverArtPath),
                    contentDescription = "Cover",
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Text("📖", fontSize = 20.sp)
            }
        }

        Spacer(Modifier.width(10.dp))

        // Book title + type
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = book.title,
                color = DarkOlive,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (book.bookType.isNotBlank()) {
                Text(
                    text = "(${book.bookType})",
                    color = DarkOlive.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                )
            }
        }

        // Edit / open action
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = MediumForestGreen,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
