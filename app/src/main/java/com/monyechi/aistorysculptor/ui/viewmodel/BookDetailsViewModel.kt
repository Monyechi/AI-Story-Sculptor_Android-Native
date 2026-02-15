package com.monyechi.aistorysculptor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.monyechi.aistorysculptor.domain.common.AppResult
import com.monyechi.aistorysculptor.domain.model.BookDetails
import com.monyechi.aistorysculptor.domain.usecase.GetBookDetailsUseCase
import com.monyechi.aistorysculptor.domain.usecase.GetDownloadUrlUseCase
import com.monyechi.aistorysculptor.data.work.DownloadBookWorker
import com.monyechi.aistorysculptor.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DownloadUiState {
    data object Idle : DownloadUiState
    data object InProgress : DownloadUiState
    data class Success(val filePath: String) : DownloadUiState
    data class Error(val message: String) : DownloadUiState
}

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val getBookDetailsUseCase: GetBookDetailsUseCase,
    private val getDownloadUrlUseCase: GetDownloadUrlUseCase,
    private val workManager: WorkManager
) : ViewModel() {

    private val _detailsState = MutableStateFlow<UiState<BookDetails>>(UiState.Loading)
    val detailsState: StateFlow<UiState<BookDetails>> = _detailsState.asStateFlow()

    private val _downloadState = MutableStateFlow<DownloadUiState>(DownloadUiState.Idle)
    val downloadState: StateFlow<DownloadUiState> = _downloadState.asStateFlow()

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

    fun downloadBook(bookId: String, bookTitle: String, format: String = DownloadBookWorker.FORMAT_PDF) {
        if (bookId.isBlank()) {
            _downloadState.value = DownloadUiState.Error("Invalid book id")
            return
        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = Data.Builder()
            .putString(DownloadBookWorker.KEY_BOOK_ID, bookId)
            .putString(DownloadBookWorker.KEY_BOOK_TITLE, bookTitle)
            .putString(DownloadBookWorker.KEY_FORMAT, format)
            .build()

        val request = OneTimeWorkRequestBuilder<DownloadBookWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .build()

        workManager.enqueue(request)
        _downloadState.value = DownloadUiState.InProgress

        viewModelScope.launch {
            workManager.getWorkInfoByIdFlow(request.id).collectLatest { info ->
                when (info.state) {
                    WorkInfo.State.ENQUEUED,
                    WorkInfo.State.RUNNING,
                    WorkInfo.State.BLOCKED -> {
                        _downloadState.value = DownloadUiState.InProgress
                    }

                    WorkInfo.State.SUCCEEDED -> {
                        val filePath = info.outputData.getString(DownloadBookWorker.KEY_FILE_PATH)
                        if (filePath.isNullOrBlank()) {
                            _downloadState.value = DownloadUiState.Error("Download completed but file not found")
                        } else {
                            _downloadState.value = DownloadUiState.Success(filePath)
                        }
                    }

                    WorkInfo.State.FAILED -> {
                        val message = info.outputData.getString(DownloadBookWorker.KEY_ERROR)
                            ?: "Download failed"
                        _downloadState.value = DownloadUiState.Error(message)
                    }

                    WorkInfo.State.CANCELLED -> {
                        _downloadState.value = DownloadUiState.Error("Download cancelled")
                    }
                }
            }
        }
    }
}
