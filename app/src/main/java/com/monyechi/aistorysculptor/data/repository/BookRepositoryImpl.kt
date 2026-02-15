package com.monyechi.aistorysculptor.data.repository

import com.monyechi.aistorysculptor.data.api.BookApi
import com.monyechi.aistorysculptor.data.api.BookDetailsDto
import com.monyechi.aistorysculptor.data.api.CreateBookRequestDto
import com.monyechi.aistorysculptor.data.api.GenerationStatusDto
import com.monyechi.aistorysculptor.data.db.BookDao
import com.monyechi.aistorysculptor.data.db.BookEntity
import com.monyechi.aistorysculptor.domain.common.AppResult
import com.monyechi.aistorysculptor.domain.model.Book
import com.monyechi.aistorysculptor.domain.model.BookChapter
import com.monyechi.aistorysculptor.domain.model.BookDetails
import com.monyechi.aistorysculptor.domain.model.CreateBookRequest
import com.monyechi.aistorysculptor.domain.model.GenerationStatus
import com.monyechi.aistorysculptor.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepositoryImpl @Inject constructor(
    private val bookApi: BookApi,
    private val bookDao: BookDao
) : BookRepository {

    override fun observeCachedBooks(): Flow<List<Book>> {
        return bookDao.observeBooks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun refreshBooks(): AppResult<Unit> {
        return try {
            val response = bookApi.getBooks()
            val mapped = response.books.map {
                BookEntity(
                    id = it.id,
                    title = it.title,
                    coverThumbnailUrl = it.coverThumbnailUrl,
                    createdAtIso = it.createdAt,
                    status = it.status
                )
            }
            bookDao.clearAll()
            bookDao.upsertAll(mapped)
            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Failed to refresh library", t)
        }
    }

    override suspend fun getCachedBookById(bookId: String): Book? {
        return bookDao.getBookById(bookId)?.toDomain()
    }

    override suspend fun createBook(request: CreateBookRequest): AppResult<GenerationStatus> {
        return try {
            val response = bookApi.createBook(
                CreateBookRequestDto(
                    title = request.title,
                    author = request.author,
                    bookType = request.bookType,
                    genre = request.genre,
                    language = request.language,
                    pov = request.pov,
                    writingStyle = request.writingStyle,
                    summary = request.summary,
                    characterName = request.characterName,
                    characterDescription = request.characterDescription
                )
            )
            AppResult.Success(
                GenerationStatus(
                    jobId = response.jobId,
                    status = response.state,
                    bookId = response.bookId,
                    message = response.message
                )
            )
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Failed to create book", t)
        }
    }

    override suspend fun getGenerationStatus(jobId: String): AppResult<GenerationStatus> {
        return try {
            val response = bookApi.getGenerationStatus(jobId)
            AppResult.Success(response.toDomain())
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Failed to fetch generation status", t)
        }
    }

    override suspend fun getBookDetails(bookId: String): AppResult<BookDetails> {
        return try {
            val response = bookApi.getBookDetails(bookId)
            AppResult.Success(response.toDomain())
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Failed to fetch book details", t)
        }
    }

    override suspend fun getDownloadUrl(bookId: String): AppResult<String> {
        return try {
            val response = bookApi.getBookDownload(bookId = bookId, format = "pdf")
            AppResult.Success(response.url)
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Download URL unavailable", t)
        }
    }

    private fun BookEntity.toDomain(): Book {
        return Book(
            id = id,
            title = title,
            coverThumbnailUrl = coverThumbnailUrl,
            createdAtIso = createdAtIso,
            status = status
        )
    }

    private fun GenerationStatusDto.toDomain(): GenerationStatus {
        return GenerationStatus(
            jobId = jobId,
            status = state,
            bookId = bookId,
            progress = progress,
            message = message
        )
    }

    private fun BookDetailsDto.toDomain(): BookDetails {
        return BookDetails(
            id = id,
            title = title,
            coverImageUrl = coverImageUrl,
            createdAtIso = createdAt,
            status = status,
            chapters = chapters.map {
                BookChapter(
                    index = it.index,
                    title = it.title,
                    content = it.content
                )
            },
            downloadUrl = downloadUrl,
            shareUrl = shareUrl
        )
    }
}
