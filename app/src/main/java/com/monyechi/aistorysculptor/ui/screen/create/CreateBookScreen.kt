package com.monyechi.aistorysculptor.ui.screen.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monyechi.aistorysculptor.ui.common.UiState
import com.monyechi.aistorysculptor.ui.viewmodel.CreateBookViewModel

@Composable
fun CreateBookScreen(
    viewModel: CreateBookViewModel,
    onGenerated: (String?) -> Unit,
    onBack: () -> Unit
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val generationState by viewModel.generationState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Create Book", style = MaterialTheme.typography.headlineSmall)

        Text("Step ${formState.currentStep + 1} of 4")

        when (formState.currentStep) {
            0 -> {
                OutlinedTextField(
                    value = formState.title,
                    onValueChange = viewModel::updateTitle,
                    label = { Text("Book Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = formState.author,
                    onValueChange = viewModel::updateAuthor,
                    label = { Text("Author") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = formState.bookType,
                    onValueChange = viewModel::updateBookType,
                    label = { Text("Book Type (children/self-help/fiction-novel/non-fiction-novel)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            1 -> {
                OutlinedTextField(
                    value = formState.genre,
                    onValueChange = viewModel::updateGenre,
                    label = { Text("Genre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = formState.language,
                    onValueChange = viewModel::updateLanguage,
                    label = { Text("Language") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = formState.pov,
                    onValueChange = viewModel::updatePov,
                    label = { Text("Point of View") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = formState.writingStyle,
                    onValueChange = viewModel::updateWritingStyle,
                    label = { Text("Writing Style") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            2 -> {
                OutlinedTextField(
                    value = formState.summary,
                    onValueChange = viewModel::updateSummary,
                    label = { Text("Story Summary") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = formState.characterName,
                    onValueChange = viewModel::updateCharacterName,
                    label = { Text("Main Character Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = formState.characterDescription,
                    onValueChange = viewModel::updateCharacterDescription,
                    label = { Text("Character Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            else -> {
                Text("Review")
                Text("Title: ${formState.title}")
                Text("Author: ${formState.author}")
                Text("Book Type: ${formState.bookType}")
                Text("Genre: ${formState.genre}")
                Text("Language: ${formState.language}")
                Text("POV: ${formState.pov}")
                Text("Style: ${formState.writingStyle}")
                Text("Summary: ${formState.summary}")
                Text("Character: ${formState.characterName}")
            }
        }

        when (val state = generationState) {
            UiState.Loading -> {
                CircularProgressIndicator()
                Text("Submitting and polling generation status...")
            }

            is UiState.Success -> {
                Text("Status: ${state.data.status}")
                state.data.progress?.let { Text("Progress: $it%") }
                state.data.message?.let { Text(it) }
            }

            is UiState.Error -> {
                Text(state.message, color = MaterialTheme.colorScheme.error)
            }

            null -> Unit
        }

        if (formState.currentStep > 0) {
            Button(onClick = viewModel::prevStep, modifier = Modifier.fillMaxWidth()) {
                Text("Back Step")
            }
        }

        if (formState.currentStep < 3) {
            Button(onClick = viewModel::nextStep, modifier = Modifier.fillMaxWidth()) {
                Text("Next Step")
            }
        } else {
            Button(
                onClick = {
                    viewModel.submitAndPoll(onCompleted = onGenerated)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Generate Book")
            }
        }

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}
