package com.monyechi.aistorysculptor.ui.screen.details

import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monyechi.aistorysculptor.data.work.DownloadBookWorker
import com.monyechi.aistorysculptor.ui.common.UiState
import com.monyechi.aistorysculptor.ui.viewmodel.BookDetailsViewModel
import com.monyechi.aistorysculptor.ui.viewmodel.DownloadUiState
import java.io.File

@Composable
fun BookDetailsScreen(
    bookId: String,
    viewModel: BookDetailsViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val detailsState by viewModel.detailsState.collectAsStateWithLifecycle()
    val downloadState by viewModel.downloadState.collectAsStateWithLifecycle()
    var downloadedFilePath by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(bookId) {
        viewModel.load(bookId)
    }

    LaunchedEffect(downloadState) {
        if (downloadState is DownloadUiState.Success) {
            downloadedFilePath = (downloadState as DownloadUiState.Success).filePath
        }
    }

    when (val state = detailsState) {
        UiState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Book Details", style = MaterialTheme.typography.headlineSmall)
                CircularProgressIndicator()
            }
        }

        is UiState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Book Details", style = MaterialTheme.typography.headlineSmall)
                Text(state.message, color = MaterialTheme.colorScheme.error)
                Button(onClick = { viewModel.load(bookId) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Retry")
                }
                Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                    Text("Back")
                }
            }
        }

        is UiState.Success -> {
            val details = state.data
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Book Details", style = MaterialTheme.typography.headlineSmall)
                    Text(details.title, style = MaterialTheme.typography.titleLarge)
                    Text("Status: ${details.status}")
                    Text("Created: ${details.createdAtIso}")
                }

                item {
                    Button(
                        enabled = downloadState !is DownloadUiState.InProgress,
                        onClick = {
                            viewModel.downloadBook(
                                bookId = details.id,
                                bookTitle = details.title,
                                format = DownloadBookWorker.FORMAT_PDF
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (downloadState is DownloadUiState.InProgress) "Downloading..." else "Download")
                    }
                }

                item {
                    Button(
                        enabled = !downloadedFilePath.isNullOrBlank() || !details.shareUrl.isNullOrBlank() || !details.downloadUrl.isNullOrBlank(),
                        onClick = {
                            val localPath = downloadedFilePath
                            if (!localPath.isNullOrBlank()) {
                                val file = File(localPath)
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    file
                                )
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/pdf"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Book"))
                            } else {
                                val shareText = details.shareUrl ?: details.downloadUrl.orEmpty()
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Book"))
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Share")
                    }
                }

                item {
                    when (val state = downloadState) {
                        DownloadUiState.Idle -> Unit
                        DownloadUiState.InProgress -> Text("Download in progress...")
                        is DownloadUiState.Success -> Text("Downloaded: ${state.filePath}")
                        is DownloadUiState.Error -> Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                items(details.chapters) { chapter ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Chapter ${chapter.index}: ${chapter.title}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(chapter.content)
                    }
                }

                item {
                    Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                        Text("Back")
                    }
                }
            }
        }
    }
}
