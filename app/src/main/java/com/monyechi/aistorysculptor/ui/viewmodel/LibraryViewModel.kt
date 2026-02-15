package com.monyechi.aistorysculptor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monyechi.aistorysculptor.domain.common.AppResult
import com.monyechi.aistorysculptor.domain.model.Book
import com.monyechi.aistorysculptor.domain.usecase.DeleteBookUseCase
import com.monyechi.aistorysculptor.domain.usecase.GetCurrentUserUseCase
import com.monyechi.aistorysculptor.domain.usecase.ObserveBooksUseCase
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
    private val observeBooksUseCase: ObserveBooksUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val deleteBookUseCase: DeleteBookUseCase,
) : ViewModel() {

    private val _booksState = MutableStateFlow<UiState<List<Book>>>(UiState.Loading)
    val booksState: StateFlow<UiState<List<Book>>> = _booksState.asStateFlow()

    init {
        loadBooks()
    }

    fun loadBooks() {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            if (user == null) {
                _booksState.value = UiState.Error("Not logged in")
                return@launch
            }
            observeBooksUseCase(user.id).collectLatest { books ->
                _booksState.value = UiState.Success(books)
            }
        }
    }

    fun deleteBook(bookId: Long) {
        viewModelScope.launch {
            deleteBookUseCase(bookId)
        }
    }
}
