package com.monyechi.aistorysculptor.domain.usecase

import com.monyechi.aistorysculptor.domain.repository.BookRepository
import com.monyechi.aistorysculptor.domain.model.CreateBookRequest
import javax.inject.Inject

class ObserveCachedBooksUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    operator fun invoke() = bookRepository.observeCachedBooks()
}

class RefreshBooksUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke() = bookRepository.refreshBooks()
}

class GetCachedBookByIdUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(bookId: String) = bookRepository.getCachedBookById(bookId)
}

class CreateBookUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(request: CreateBookRequest) = bookRepository.createBook(request)
}

class GetGenerationStatusUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(jobId: String) = bookRepository.getGenerationStatus(jobId)
}

class GetBookDetailsUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(bookId: String) = bookRepository.getBookDetails(bookId)
}

class GetDownloadUrlUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(bookId: String) = bookRepository.getDownloadUrl(bookId)
}
