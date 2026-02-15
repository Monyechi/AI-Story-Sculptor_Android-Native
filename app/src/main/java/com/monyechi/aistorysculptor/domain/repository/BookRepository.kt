package com.monyechi.aistorysculptor.domain.repository

import com.monyechi.aistorysculptor.domain.common.AppResult
import com.monyechi.aistorysculptor.domain.model.Book
import com.monyechi.aistorysculptor.domain.model.BookDetails
import com.monyechi.aistorysculptor.domain.model.CreateBookRequest
import com.monyechi.aistorysculptor.domain.model.GenerationStatus
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    fun observeCachedBooks(): Flow<List<Book>>
    suspend fun refreshBooks(): AppResult<Unit>
    suspend fun getCachedBookById(bookId: String): Book?
    suspend fun createBook(request: CreateBookRequest): AppResult<GenerationStatus>
    suspend fun getGenerationStatus(jobId: String): AppResult<GenerationStatus>
    suspend fun getBookDetails(bookId: String): AppResult<BookDetails>
    suspend fun getDownloadUrl(bookId: String): AppResult<String>
}
