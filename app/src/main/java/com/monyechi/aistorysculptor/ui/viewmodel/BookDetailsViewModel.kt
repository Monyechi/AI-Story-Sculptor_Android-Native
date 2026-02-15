package com.monyechi.aistorysculptor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monyechi.aistorysculptor.domain.common.AppResult
import com.monyechi.aistorysculptor.domain.model.BookDetails
import com.monyechi.aistorysculptor.domain.usecase.GetBookDetailsUseCase
import com.monyechi.aistorysculptor.domain.usecase.GetDownloadUrlUseCase
import com.monyechi.aistorysculptor.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val getBookDetailsUseCase: GetBookDetailsUseCase,
    private val getDownloadUrlUseCase: GetDownloadUrlUseCase
) : ViewModel() {

    private val _detailsState = MutableStateFlow<UiState<BookDetails>>(UiState.Loading)
    val detailsState: StateFlow<UiState<BookDetails>> = _detailsState.asStateFlow()

    fun load(bookId: String) {
        if (bookId.isBlank()) {
            _detailsState.value = UiState.Error("Invalid book id")
            return
        }

        viewModelScope.launch {
            _detailsState.value = UiState.Loading
            when (val detailsResult = getBookDetailsUseCase(bookId)) {
                is AppResult.Success -> {
                    val details = detailsResult.data
                    if (!details.downloadUrl.isNullOrBlank()) {
                        _detailsState.value = UiState.Success(details)
                    } else {
                        when (val downloadResult = getDownloadUrlUseCase(bookId)) {
                            is AppResult.Success -> {
                                _detailsState.value = UiState.Success(
                                    details.copy(downloadUrl = downloadResult.data)
                                )
                            }

                            is AppResult.Failure -> {
                                _detailsState.value = UiState.Success(details)
                            }
                        }
                    }
                }

                is AppResult.Failure -> {
                    _detailsState.value = UiState.Error(detailsResult.message)
                }
            }
        }
    }
}
