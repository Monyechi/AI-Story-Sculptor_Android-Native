package com.monyechi.aistorysculptor.domain.repository

import com.monyechi.aistorysculptor.domain.common.AppResult
import com.monyechi.aistorysculptor.domain.model.Book
import com.monyechi.aistorysculptor.domain.model.BookDetails
import com.monyechi.aistorysculptor.domain.model.Chapter
import com.monyechi.aistorysculptor.domain.model.Character
import com.monyechi.aistorysculptor.domain.model.CreateBookRequest
import com.monyechi.aistorysculptor.domain.model.RenderProgress
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    // ── Books ────────────────────────────────────────────────────────
    fun observeBooks(userId: Long): Flow<List<Book>>
    suspend fun createBook(userId: Long, request: CreateBookRequest): AppResult<Book>
    suspend fun getBookDetails(bookId: Long): AppResult<BookDetails>
    suspend fun deleteBook(bookId: Long): AppResult<Unit>
    suspend fun generateBookSummary(bookId: Long): AppResult<String>

    // ── Chapters ─────────────────────────────────────────────────────
    fun observeChapters(bookId: Long): Flow<List<Chapter>>
    suspend fun addChapter(bookId: Long, title: String, tone: String?, setting: String?, summary: String, desiredWordCount: Int): AppResult<Chapter>
    suspend fun autoGenerateChapter(bookId: Long, count: Int = 1): AppResult<List<Chapter>>
    suspend fun generateChapterSummary(chapterId: Long): AppResult<String>
    suspend fun renderChapter(bookId: Long, chapterId: Long): AppResult<Chapter>
    suspend fun renderAllChapters(bookId: Long): Flow<RenderProgress>
    suspend fun deleteChapter(chapterId: Long): AppResult<Unit>

    // ── Characters ───────────────────────────────────────────────────
    fun observeCharacters(bookId: Long): Flow<List<Character>>
    suspend fun addCharacter(bookId: Long, name: String, age: Int?, bio: String?, role: String): AppResult<Character>
    suspend fun autoGenerateCharacter(bookId: Long): AppResult<Character>
    suspend fun deleteCharacter(characterId: Long): AppResult<Unit>

    // ── Cover Art ────────────────────────────────────────────────────
    suspend fun generateCoverArt(bookId: Long, userDescription: String? = null): AppResult<String>
    suspend fun saveCoverArt(bookId: Long, base64Data: String): AppResult<String>
    suspend fun removeCoverArt(bookId: Long): AppResult<Unit>
}
