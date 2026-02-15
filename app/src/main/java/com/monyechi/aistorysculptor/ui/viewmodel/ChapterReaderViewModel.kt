package com.monyechi.aistorysculptor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monyechi.aistorysculptor.domain.common.AppResult
import com.monyechi.aistorysculptor.domain.model.Chapter
import com.monyechi.aistorysculptor.domain.usecase.GetBookDetailsUseCase
import com.monyechi.aistorysculptor.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChapterReaderViewModel @Inject constructor(
    private val getBookDetailsUseCase: GetBookDetailsUseCase,
) : ViewModel() {

    private val _chapterState = MutableStateFlow<UiState<Chapter>>(UiState.Loading)
    val chapterState: StateFlow<UiState<Chapter>> = _chapterState.asStateFlow()

    fun load(bookId: Long, chapterId: Long) {
        viewModelScope.launch {
            _chapterState.value = UiState.Loading
            when (val result = getBookDetailsUseCase(bookId)) {
                is AppResult.Failure -> _chapterState.value = UiState.Error(result.message)
                is AppResult.Success -> {
                    val chapter = result.data.chapters.firstOrNull { it.id == chapterId }
                    when {
                        chapter == null -> _chapterState.value = UiState.Error("Chapter not found")
                        chapter.renderedContent.isNullOrBlank() -> _chapterState.value = UiState.Error("Chapter is not rendered yet")
                        else -> _chapterState.value = UiState.Success(chapter)
                    }
                }
            }
        }
    }
}
