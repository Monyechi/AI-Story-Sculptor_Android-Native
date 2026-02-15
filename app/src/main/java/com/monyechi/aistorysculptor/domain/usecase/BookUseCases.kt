package com.monyechi.aistorysculptor.domain.usecase

import com.monyechi.aistorysculptor.domain.model.CreateBookRequest
import com.monyechi.aistorysculptor.domain.repository.BookRepository
import javax.inject.Inject

// ── Books ────────────────────────────────────────────────────────────

class ObserveBooksUseCase @Inject constructor(
    private val repo: BookRepository
) {
    operator fun invoke(userId: Long) = repo.observeBooks(userId)
}

class CreateBookUseCase @Inject constructor(
    private val repo: BookRepository
) {
    suspend operator fun invoke(userId: Long, request: CreateBookRequest) =
        repo.createBook(userId, request)
}

class GetBookDetailsUseCase @Inject constructor(
    private val repo: BookRepository
) {
    suspend operator fun invoke(bookId: Long) = repo.getBookDetails(bookId)
}

class DeleteBookUseCase @Inject constructor(
    private val repo: BookRepository
) {
    suspend operator fun invoke(bookId: Long) = repo.deleteBook(bookId)
}

class GenerateBookSummaryUseCase @Inject constructor(
    private val repo: BookRepository
) {
    suspend operator fun invoke(bookId: Long) = repo.generateBookSummary(bookId)
}

// ── Chapters ─────────────────────────────────────────────────────────

class ObserveChaptersUseCase @Inject constructor(
    private val repo: BookRepository
) {
    operator fun invoke(bookId: Long) = repo.observeChapters(bookId)
}

class AddChapterUseCase @Inject constructor(
    private val repo: BookRepository
) {
    suspend operator fun invoke(
        bookId: Long, title: String, tone: String?, setting: String?,
        summary: String, desiredWordCount: Int
    ) = repo.addChapter(bookId, title, tone, setting, summary, desiredWordCount)
}

class AutoGenerateChapterUseCase @Inject constructor(
    private val repo: BookRepository
) {
    suspend operator fun invoke(bookId: Long, count: Int = 1) =
        repo.autoGenerateChapter(bookId, count)
}

class GenerateChapterSummaryUseCase @Inject constructor(
    private val repo: BookRepository
) {
    suspend operator fun invoke(chapterId: Long) = repo.generateChapterSummary(chapterId)
}

class RenderChapterUseCase @Inject constructor(
    private val repo: BookRepository
) {
    suspend operator fun invoke(bookId: Long, chapterId: Long) =
        repo.renderChapter(bookId, chapterId)
}

class RenderAllChaptersUseCase @Inject constructor(
    private val repo: BookRepository
) {
    suspend operator fun invoke(bookId: Long) = repo.renderAllChapters(bookId)
}

class DeleteChapterUseCase @Inject constructor(
    private val repo: BookRepository
) {
    suspend operator fun invoke(chapterId: Long) = repo.deleteChapter(chapterId)
}

// ── Characters ───────────────────────────────────────────────────────

class ObserveCharactersUseCase @Inject constructor(
    private val repo: BookRepository
) {
    operator fun invoke(bookId: Long) = repo.observeCharacters(bookId)
}

class AddCharacterUseCase @Inject constructor(
    private val repo: BookRepository
) {
    suspend operator fun invoke(
        bookId: Long, name: String, age: Int?, bio: String?, role: String
    ) = repo.addCharacter(bookId, name, age, bio, role)
}

class AutoGenerateCharacterUseCase @Inject constructor(
    private val repo: BookRepository
) {
    suspend operator fun invoke(bookId: Long) = repo.autoGenerateCharacter(bookId)
}

class DeleteCharacterUseCase @Inject constructor(
    private val repo: BookRepository
) {
    suspend operator fun invoke(characterId: Long) = repo.deleteCharacter(characterId)
}

// ── Cover Art ────────────────────────────────────────────────────────

class GenerateCoverArtUseCase @Inject constructor(
    private val repo: BookRepository
) {
    suspend operator fun invoke(bookId: Long, userDescription: String? = null) =
        repo.generateCoverArt(bookId, userDescription)
}

class SaveCoverArtUseCase @Inject constructor(
    private val repo: BookRepository
) {
    suspend operator fun invoke(bookId: Long, base64Data: String) =
        repo.saveCoverArt(bookId, base64Data)
}

class RemoveCoverArtUseCase @Inject constructor(
    private val repo: BookRepository
) {
    suspend operator fun invoke(bookId: Long) = repo.removeCoverArt(bookId)
}
