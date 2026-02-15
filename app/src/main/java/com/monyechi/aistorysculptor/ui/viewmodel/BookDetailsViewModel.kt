package com.monyechi.aistorysculptor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monyechi.aistorysculptor.domain.common.AppResult
import com.monyechi.aistorysculptor.domain.model.BookDetails
import com.monyechi.aistorysculptor.domain.model.Chapter
import com.monyechi.aistorysculptor.domain.model.Character
import com.monyechi.aistorysculptor.domain.model.RenderProgress
import com.monyechi.aistorysculptor.domain.usecase.AutoGenerateChapterUseCase
import com.monyechi.aistorysculptor.domain.usecase.AutoGenerateCharacterUseCase
import com.monyechi.aistorysculptor.domain.usecase.DeleteChapterUseCase
import com.monyechi.aistorysculptor.domain.usecase.DeleteCharacterUseCase
import com.monyechi.aistorysculptor.domain.usecase.GenerateBookSummaryUseCase
import com.monyechi.aistorysculptor.domain.usecase.GenerateCoverArtUseCase
import com.monyechi.aistorysculptor.domain.usecase.GetBookDetailsUseCase
import com.monyechi.aistorysculptor.domain.usecase.RenderAllChaptersUseCase
import com.monyechi.aistorysculptor.domain.usecase.RenderChapterUseCase
import com.monyechi.aistorysculptor.domain.usecase.SaveCoverArtUseCase
import com.monyechi.aistorysculptor.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val getBookDetailsUseCase: GetBookDetailsUseCase,
    private val generateBookSummaryUseCase: GenerateBookSummaryUseCase,
    private val autoGenerateChapterUseCase: AutoGenerateChapterUseCase,
    private val autoGenerateCharacterUseCase: AutoGenerateCharacterUseCase,
    private val renderChapterUseCase: RenderChapterUseCase,
    private val renderAllChaptersUseCase: RenderAllChaptersUseCase,
    private val deleteChapterUseCase: DeleteChapterUseCase,
    private val deleteCharacterUseCase: DeleteCharacterUseCase,
    private val generateCoverArtUseCase: GenerateCoverArtUseCase,
    private val saveCoverArtUseCase: SaveCoverArtUseCase,
) : ViewModel() {

    private val _detailsState = MutableStateFlow<UiState<BookDetails>>(UiState.Loading)
    val detailsState: StateFlow<UiState<BookDetails>> = _detailsState.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    private val _renderProgress = MutableStateFlow<RenderProgress?>(null)
    val renderProgress: StateFlow<RenderProgress?> = _renderProgress.asStateFlow()

    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()

    fun load(bookId: Long) {
        viewModelScope.launch {
            _detailsState.value = UiState.Loading
            when (val result = getBookDetailsUseCase(bookId)) {
                is AppResult.Success -> _detailsState.value = UiState.Success(result.data)
                is AppResult.Failure -> _detailsState.value = UiState.Error(result.message)
            }
        }
    }

    fun generateSummary(bookId: Long) {
        viewModelScope.launch {
            _isBusy.value = true
            when (val result = generateBookSummaryUseCase(bookId)) {
                is AppResult.Success -> {
                    _actionMessage.value = "Summary generated"
                    load(bookId)
                }
                is AppResult.Failure -> _actionMessage.value = result.message
            }
            _isBusy.value = false
        }
    }

    fun autoGenerateChapters(bookId: Long, count: Int = 1) {
        viewModelScope.launch {
            _isBusy.value = true
            when (val result = autoGenerateChapterUseCase(bookId, count)) {
                is AppResult.Success -> {
                    _actionMessage.value = "${result.data.size} chapter(s) generated"
                    load(bookId)
                }
                is AppResult.Failure -> _actionMessage.value = result.message
            }
            _isBusy.value = false
        }
    }

    fun autoGenerateCharacter(bookId: Long) {
        viewModelScope.launch {
            _isBusy.value = true
            when (val result = autoGenerateCharacterUseCase(bookId)) {
                is AppResult.Success -> {
                    _actionMessage.value = "Character \"${result.data.name}\" generated"
                    load(bookId)
                }
                is AppResult.Failure -> _actionMessage.value = result.message
            }
            _isBusy.value = false
        }
    }

    fun renderChapter(bookId: Long, chapterId: Long) {
        viewModelScope.launch {
            _isBusy.value = true
            when (val result = renderChapterUseCase(bookId, chapterId)) {
                is AppResult.Success -> {
                    _actionMessage.value = "Chapter rendered"
                    load(bookId)
                }
                is AppResult.Failure -> _actionMessage.value = result.message
            }
            _isBusy.value = false
        }
    }

    fun renderAllChapters(bookId: Long) {
        viewModelScope.launch {
            _isBusy.value = true
            try {
                renderAllChaptersUseCase(bookId).collectLatest { progress ->
                    _renderProgress.value = progress
                }
                _actionMessage.value = "All chapters rendered"
                load(bookId)
            } catch (t: Throwable) {
                _actionMessage.value = t.message ?: "Render failed"
            }
            _renderProgress.value = null
            _isBusy.value = false
        }
    }

    fun deleteChapter(bookId: Long, chapterId: Long) {
        viewModelScope.launch {
            deleteChapterUseCase(chapterId)
            load(bookId)
        }
    }

    fun deleteCharacter(bookId: Long, characterId: Long) {
        viewModelScope.launch {
            deleteCharacterUseCase(characterId)
            load(bookId)
        }
    }

    fun generateCoverArt(bookId: Long, userDescription: String? = null) {
        viewModelScope.launch {
            _isBusy.value = true
            when (val result = generateCoverArtUseCase(bookId, userDescription)) {
                is AppResult.Success -> {
                    // Save the base64 to disk
                    when (val saveResult = saveCoverArtUseCase(bookId, result.data)) {
                        is AppResult.Success -> {
                            _actionMessage.value = "Cover art saved"
                            load(bookId)
                        }
                        is AppResult.Failure -> _actionMessage.value = saveResult.message
                    }
                }
                is AppResult.Failure -> _actionMessage.value = result.message
            }
            _isBusy.value = false
        }
    }

    fun consumeMessage() {
        _actionMessage.value = null
    }
}
