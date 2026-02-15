package com.monyechi.aistorysculptor.ui.screen.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monyechi.aistorysculptor.ui.common.UiState
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

    when (val state = chapterState) {
        UiState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("Chapter Reader", style = MaterialTheme.typography.headlineSmall)
                CircularProgressIndicator()
            }
        }

        is UiState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("Chapter Reader", style = MaterialTheme.typography.headlineSmall)
                Text(state.message, color = MaterialTheme.colorScheme.error)
                Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
            }
        }

        is UiState.Success -> {
            val chapter = state.data
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    Text(
                        text = "Chapter ${chapter.chapterNum}: ${chapter.title}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "${chapter.wordCount} words",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }

                items(chapter.renderedContent.orEmpty().split("\n")) { paragraph ->
                    if (paragraph.isNotBlank()) {
                        Text(
                            text = paragraph,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }

                item {
                    Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
                }
            }
        }
    }
}
