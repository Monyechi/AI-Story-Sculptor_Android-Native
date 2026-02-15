package com.monyechi.aistorysculptor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monyechi.aistorysculptor.domain.common.AppResult
import com.monyechi.aistorysculptor.domain.model.Book
import com.monyechi.aistorysculptor.domain.usecase.ObserveCachedBooksUseCase
import com.monyechi.aistorysculptor.domain.usecase.RefreshBooksUseCase
import com.monyechi.aistorysculptor.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val observeCachedBooksUseCase: ObserveCachedBooksUseCase,
    private val refreshBooksUseCase: RefreshBooksUseCase
) : ViewModel() {

    private val _booksState = MutableStateFlow<UiState<List<Book>>>(UiState.Loading)
    val booksState: StateFlow<UiState<List<Book>>> = _booksState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        observeCachedBooks()
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            when (val result = refreshBooksUseCase()) {
                is AppResult.Success -> Unit
                is AppResult.Failure -> {
                    val current = (_booksState.value as? UiState.Success)?.data.orEmpty()
                    if (current.isEmpty()) {
                        _booksState.value = UiState.Error(result.message)
                    }
                }
            }
            _isRefreshing.value = false
        }
    }

    private fun observeCachedBooks() {
        viewModelScope.launch {
            observeCachedBooksUseCase().collectLatest { books ->
                _booksState.value = UiState.Success(books)
            }
        }
    }
}
