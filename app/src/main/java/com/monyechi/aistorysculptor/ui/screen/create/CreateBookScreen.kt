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
                    value = formState.genre,
                    onValueChange = viewModel::updateGenre,
                    label = { Text("Genre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = formState.ageGroup,
                    onValueChange = viewModel::updateAgeGroup,
                    label = { Text("Age Group") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            1 -> {
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

            2 -> {
                OutlinedTextField(
                    value = formState.storyOutline,
                    onValueChange = viewModel::updateStoryOutline,
                    label = { Text("Story Outline") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            else -> {
                Text("Review")
                Text("Genre: ${formState.genre}")
                Text("Age Group: ${formState.ageGroup}")
                Text("Character: ${formState.characterName}")
                Text("Outline: ${formState.storyOutline}")
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
