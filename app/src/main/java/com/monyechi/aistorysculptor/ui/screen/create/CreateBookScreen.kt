package com.monyechi.aistorysculptor.ui.screen.create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monyechi.aistorysculptor.ui.common.AppScaffold
import com.monyechi.aistorysculptor.ui.common.BannerButton
import com.monyechi.aistorysculptor.ui.common.CoralButton
import com.monyechi.aistorysculptor.ui.common.DangerButton
import com.monyechi.aistorysculptor.ui.common.DarkContainer
import com.monyechi.aistorysculptor.ui.common.ForestOutlinedButton
import com.monyechi.aistorysculptor.ui.common.UiState
import com.monyechi.aistorysculptor.ui.theme.*
import com.monyechi.aistorysculptor.ui.viewmodel.CreateBookViewModel

private val bookTypes = listOf(
    "children" to "Children's Book",
    "self-help" to "Self-Help",
    "fiction-novel" to "Fiction Novel",
    "non-fiction-novel" to "Non-Fiction Novel",
)

@Composable
fun CreateBookScreen(
    viewModel: CreateBookViewModel,
    onCreated: (Long) -> Unit,
    onBack: () -> Unit,
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val createState by viewModel.createState.collectAsStateWithLifecycle()

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = White,
        unfocusedTextColor = White,
        focusedBorderColor = OliveAccent,
        unfocusedBorderColor = Beige.copy(alpha = 0.5f),
        focusedLabelColor = OliveAccent,
        unfocusedLabelColor = Beige.copy(alpha = 0.7f),
        cursorColor = OliveAccent,
    )

    AppScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // ── Dark green module container (matching .module) ──
            DarkContainer(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 15.dp,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    // Header
                    Text(
                        text = "Create New Book",
                        style = MaterialTheme.typography.headlineMedium,
                        color = White,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Step ${formState.currentStep + 1} of 4",
                        color = Beige,
                        fontSize = 14.sp,
                    )

                    Spacer(Modifier.height(4.dp))

                    when (formState.currentStep) {
                        0 -> {
                            // Title
                            FormLabel("Title:")
                            OutlinedTextField(
                                value = formState.title,
                                onValueChange = viewModel::updateTitle,
                                placeholder = { Text("Enter book title", color = Beige.copy(alpha = 0.5f)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = fieldColors,
                                singleLine = true,
                            )

                            // Author
                            FormLabel("Author:")
                            OutlinedTextField(
                                value = formState.author,
                                onValueChange = viewModel::updateAuthor,
                                placeholder = { Text("Enter author name", color = Beige.copy(alpha = 0.5f)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = fieldColors,
                                singleLine = true,
                            )

                            // Book Type — radio buttons matching the web app's dropdown/radio
                            FormLabel("Book Type:")
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        White.copy(alpha = 0.08f),
                                        RoundedCornerShape(8.dp),
                                    )
                                    .padding(8.dp),
                            ) {
                                bookTypes.forEach { (value, label) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.updateBookType(value) }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        RadioButton(
                                            selected = formState.bookType == value,
                                            onClick = { viewModel.updateBookType(value) },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = AccentGreen,
                                                unselectedColor = Beige.copy(alpha = 0.7f),
                                            ),
                                        )
                                        Text(label, color = White, fontSize = 16.sp)
                                    }
                                }
                            }
                        }

                        1 -> {
                            FormLabel("Genre:")
                            OutlinedTextField(
                                value = formState.genre,
                                onValueChange = viewModel::updateGenre,
                                placeholder = { Text("e.g. Fantasy, Mystery", color = Beige.copy(alpha = 0.5f)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = fieldColors,
                                singleLine = true,
                            )

                            FormLabel("Language:")
                            OutlinedTextField(
                                value = formState.language,
                                onValueChange = viewModel::updateLanguage,
                                placeholder = { Text("e.g. English", color = Beige.copy(alpha = 0.5f)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = fieldColors,
                                singleLine = true,
                            )

                            FormLabel("Point of View:")
                            OutlinedTextField(
                                value = formState.pov,
                                onValueChange = viewModel::updatePov,
                                placeholder = { Text("e.g. Third person", color = Beige.copy(alpha = 0.5f)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = fieldColors,
                                singleLine = true,
                            )

                            FormLabel("Writing Style:")
                            OutlinedTextField(
                                value = formState.writingStyle,
                                onValueChange = viewModel::updateWritingStyle,
                                placeholder = { Text("e.g. Descriptive, Humorous", color = Beige.copy(alpha = 0.5f)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = fieldColors,
                                singleLine = true,
                            )
                        }

                        2 -> {
                            FormLabel("Story Summary:")
                            OutlinedTextField(
                                value = formState.summary,
                                onValueChange = viewModel::updateSummary,
                                placeholder = { Text("Leave blank to auto-generate", color = Beige.copy(alpha = 0.5f)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = fieldColors,
                                minLines = 5,
                            )
                        }

                        else -> {
                            // Review step
                            Text("Review", style = MaterialTheme.typography.titleLarge, color = AccentGreen)
                            Spacer(Modifier.height(8.dp))
                            ReviewRow("Title", formState.title)
                            ReviewRow("Author", formState.author)
                            ReviewRow("Book Type", bookTypes.find { it.first == formState.bookType }?.second ?: formState.bookType)
                            ReviewRow("Genre", formState.genre)
                            ReviewRow("Language", formState.language)
                            ReviewRow("POV", formState.pov)
                            ReviewRow("Style", formState.writingStyle)
                            if (formState.summary.isNotBlank()) {
                                ReviewRow("Summary", formState.summary)
                            }
                        }
                    }

                    // Status messages
                    when (val state = createState) {
                        UiState.Loading -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                CircularProgressIndicator(color = AccentGreen, modifier = Modifier.padding(4.dp))
                                Text("Creating book...", color = Beige)
                            }
                        }
                        is UiState.Success -> Text("Book created!", color = AccentGreen, fontWeight = FontWeight.Bold)
                        is UiState.Error -> Text(state.message, color = DangerRed, fontSize = 13.sp)
                        null -> Unit
                    }

                    Spacer(Modifier.height(8.dp))

                    // Navigation buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        if (formState.currentStep > 0) {
                            ForestOutlinedButton(
                                text = "Back",
                                onClick = viewModel::prevStep,
                                modifier = Modifier.weight(1f),
                            )
                        }

                        if (formState.currentStep < 3) {
                            BannerButton(
                                text = "Next",
                                onClick = viewModel::nextStep,
                                modifier = Modifier.weight(1f),
                            )
                        } else {
                            CoralButton(
                                text = "Create Book",
                                onClick = { viewModel.submit(onCreated = onCreated) },
                                enabled = createState !is UiState.Loading,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }

                    DangerButton(
                        text = "Cancel",
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(
        text = text,
        color = White,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
    )
}

@Composable
private fun ReviewRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
    ) {
        Text("$label: ", color = Beige, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(value.ifBlank { "—" }, color = White, fontSize = 14.sp)
    }
}
