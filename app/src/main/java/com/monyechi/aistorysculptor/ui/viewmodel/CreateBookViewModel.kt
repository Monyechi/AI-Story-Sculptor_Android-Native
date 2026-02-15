package com.monyechi.aistorysculptor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monyechi.aistorysculptor.domain.common.AppResult
import com.monyechi.aistorysculptor.domain.model.Book
import com.monyechi.aistorysculptor.domain.model.CreateBookRequest
import com.monyechi.aistorysculptor.domain.usecase.CreateBookUseCase
import com.monyechi.aistorysculptor.domain.usecase.GetCurrentUserUseCase
import com.monyechi.aistorysculptor.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateBookFormState(
    val title: String = "",
    val author: String = "",
    val bookType: String = "fiction-novel",
    val genre: String = "",
    val language: String = "English",
    val pov: String = "Third Person Limited",
    val writingStyle: String = "descriptive",
    val summary: String = "",
    val currentStep: Int = 0,
)

@HiltViewModel
class CreateBookViewModel @Inject constructor(
    private val createBookUseCase: CreateBookUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : ViewModel() {

    private val _formState = MutableStateFlow(CreateBookFormState())
    val formState: StateFlow<CreateBookFormState> = _formState.asStateFlow()

    private val _createState = MutableStateFlow<UiState<Book>?>(null)
    val createState: StateFlow<UiState<Book>?> = _createState.asStateFlow()

    fun updateTitle(value: String) { _formState.value = _formState.value.copy(title = value) }
    fun updateAuthor(value: String) { _formState.value = _formState.value.copy(author = value) }
    fun updateBookType(value: String) { _formState.value = _formState.value.copy(bookType = value) }
    fun updateGenre(value: String) { _formState.value = _formState.value.copy(genre = value) }
    fun updateLanguage(value: String) { _formState.value = _formState.value.copy(language = value) }
    fun updatePov(value: String) { _formState.value = _formState.value.copy(pov = value) }
    fun updateWritingStyle(value: String) { _formState.value = _formState.value.copy(writingStyle = value) }
    fun updateSummary(value: String) { _formState.value = _formState.value.copy(summary = value) }

    fun nextStep() {
        val next = (_formState.value.currentStep + 1).coerceAtMost(3)
        _formState.value = _formState.value.copy(currentStep = next)
    }

    fun prevStep() {
        val prev = (_formState.value.currentStep - 1).coerceAtLeast(0)
        _formState.value = _formState.value.copy(currentStep = prev)
    }

    fun submit(onCreated: (bookId: Long) -> Unit) {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            if (user == null) {
                _createState.value = UiState.Error("Not logged in")
                return@launch
            }

            val form = _formState.value
            val request = CreateBookRequest(
                title = form.title,
                author = form.author,
                bookType = form.bookType,
                genre = form.genre,
                language = form.language,
                pov = form.pov,
                writingStyle = form.writingStyle,
                summary = form.summary,
            )

            _createState.value = UiState.Loading
            when (val result = createBookUseCase(user.id, request)) {
                is AppResult.Success -> {
                    _createState.value = UiState.Success(result.data)
                    onCreated(result.data.id)
                }
                is AppResult.Failure -> {
                    _createState.value = UiState.Error(result.message)
                }
            }
        }
    }

    fun clearState() {
        _createState.value = null
    }
}
