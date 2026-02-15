package com.monyechi.aistorysculptor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monyechi.aistorysculptor.domain.common.AppResult
import com.monyechi.aistorysculptor.domain.model.CreateBookRequest
import com.monyechi.aistorysculptor.domain.model.GenerationStatus
import com.monyechi.aistorysculptor.domain.usecase.CreateBookUseCase
import com.monyechi.aistorysculptor.domain.usecase.GetGenerationStatusUseCase
import com.monyechi.aistorysculptor.domain.usecase.RefreshBooksUseCase
import com.monyechi.aistorysculptor.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateBookFormState(
    val genre: String = "",
    val ageGroup: String = "",
    val characterName: String = "",
    val characterDescription: String = "",
    val storyOutline: String = "",
    val currentStep: Int = 0
)

@HiltViewModel
class CreateBookViewModel @Inject constructor(
    private val createBookUseCase: CreateBookUseCase,
    private val getGenerationStatusUseCase: GetGenerationStatusUseCase,
    private val refreshBooksUseCase: RefreshBooksUseCase
) : ViewModel() {

    private val _formState = MutableStateFlow(CreateBookFormState())
    val formState: StateFlow<CreateBookFormState> = _formState.asStateFlow()

    private val _generationState = MutableStateFlow<UiState<GenerationStatus>?>(null)
    val generationState: StateFlow<UiState<GenerationStatus>?> = _generationState.asStateFlow()

    fun updateGenre(value: String) {
        _formState.value = _formState.value.copy(genre = value)
    }

    fun updateAgeGroup(value: String) {
        _formState.value = _formState.value.copy(ageGroup = value)
    }

    fun updateCharacterName(value: String) {
        _formState.value = _formState.value.copy(characterName = value)
    }

    fun updateCharacterDescription(value: String) {
        _formState.value = _formState.value.copy(characterDescription = value)
    }

    fun updateStoryOutline(value: String) {
        _formState.value = _formState.value.copy(storyOutline = value)
    }

    fun nextStep() {
        val next = (_formState.value.currentStep + 1).coerceAtMost(3)
        _formState.value = _formState.value.copy(currentStep = next)
    }

    fun prevStep() {
        val prev = (_formState.value.currentStep - 1).coerceAtLeast(0)
        _formState.value = _formState.value.copy(currentStep = prev)
    }

    fun submitAndPoll(onCompleted: (bookId: String?) -> Unit) {
        viewModelScope.launch {
            val form = _formState.value
            val request = CreateBookRequest(
                genre = form.genre,
                ageGroup = form.ageGroup,
                characterName = form.characterName,
                characterDescription = form.characterDescription,
                storyOutline = form.storyOutline
            )

            _generationState.value = UiState.Loading
            when (val createResult = createBookUseCase(request)) {
                is AppResult.Success -> {
                    _generationState.value = UiState.Success(createResult.data)
                    pollGenerationStatus(
                        jobId = createResult.data.jobId,
                        onCompleted = onCompleted
                    )
                }

                is AppResult.Failure -> {
                    _generationState.value = UiState.Error(createResult.message)
                }
            }
        }
    }

    private suspend fun pollGenerationStatus(
        jobId: String,
        onCompleted: (bookId: String?) -> Unit
    ) {
        repeat(30) {
            delay(4_000)
            when (val statusResult = getGenerationStatusUseCase(jobId)) {
                is AppResult.Success -> {
                    _generationState.value = UiState.Success(statusResult.data)
                    if (statusResult.data.isComplete()) {
                        refreshBooksUseCase()
                        onCompleted(statusResult.data.bookId)
                        return
                    }
                }

                is AppResult.Failure -> {
                    _generationState.value = UiState.Error(statusResult.message)
                    return
                }
            }
        }

        _generationState.value = UiState.Error("Generation timed out. Please retry.")
    }
}
