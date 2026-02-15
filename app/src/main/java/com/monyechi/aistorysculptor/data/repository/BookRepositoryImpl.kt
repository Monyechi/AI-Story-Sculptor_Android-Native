package com.monyechi.aistorysculptor.data.repository

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import com.monyechi.aistorysculptor.data.db.BookDao
import com.monyechi.aistorysculptor.data.db.BookEntity
import com.monyechi.aistorysculptor.data.db.ChapterDao
import com.monyechi.aistorysculptor.data.db.ChapterEntity
import com.monyechi.aistorysculptor.data.db.CharacterDao
import com.monyechi.aistorysculptor.data.db.CharacterEntity
import com.monyechi.aistorysculptor.data.db.UserDao
import com.monyechi.aistorysculptor.data.generation.ChapterRenderer
import com.monyechi.aistorysculptor.data.generation.ContentGenerator
import com.monyechi.aistorysculptor.domain.common.AppResult
import com.monyechi.aistorysculptor.domain.model.Book
import com.monyechi.aistorysculptor.domain.model.BookConstants
import com.monyechi.aistorysculptor.domain.model.BookDetails
import com.monyechi.aistorysculptor.domain.model.Chapter
import com.monyechi.aistorysculptor.domain.model.Character
import com.monyechi.aistorysculptor.domain.model.CreateBookRequest
import com.monyechi.aistorysculptor.domain.model.RenderProgress
import com.monyechi.aistorysculptor.domain.repository.BookRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bookDao: BookDao,
    private val chapterDao: ChapterDao,
    private val characterDao: CharacterDao,
    private val userDao: UserDao,
    private val chapterRenderer: ChapterRenderer,
    private val contentGenerator: ContentGenerator,
) : BookRepository {

    // ── Books ────────────────────────────────────────────────────────

    override fun observeBooks(userId: Long): Flow<List<Book>> {
        return bookDao.observeBooksByUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun createBook(userId: Long, request: CreateBookRequest): AppResult<Book> {
        return try {
            val entity = BookEntity(
                userId = userId,
                title = request.title,
                author = request.author,
                bookType = request.bookType,
                genre = request.genre,
                language = request.language,
                pov = request.pov,
                writingStyle = request.writingStyle,
                summary = request.summary,
                createdAtIso = Instant.now().toString(),
            )
            val bookId = bookDao.insert(entity)
            val saved = bookDao.getBookById(bookId)!!
            AppResult.Success(saved.toDomain())
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Failed to create book", t)
        }
    }

    override suspend fun getBookDetails(bookId: Long): AppResult<BookDetails> {
        return try {
            val bookEntity = bookDao.getBookById(bookId)
                ?: return AppResult.Failure("Book not found")
            val chapters = chapterDao.getChaptersByBook(bookId).map { it.toDomain() }
            val characters = characterDao.getCharactersByBook(bookId).map { it.toDomain() }
            AppResult.Success(
                BookDetails(
                    book = bookEntity.toDomain(),
                    chapters = chapters,
                    characters = characters,
                )
            )
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Failed to load details", t)
        }
    }

    override suspend fun deleteBook(bookId: Long): AppResult<Unit> {
        return try {
            bookDao.deleteBook(bookId)
            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Failed to delete book", t)
        }
    }

    override suspend fun generateBookSummary(bookId: Long): AppResult<String> {
        return try {
            val book = bookDao.getBookById(bookId)
                ?: return AppResult.Failure("Book not found")
            val tokenCost = BookConstants.TOKEN_COST_GENERATE_SUMMARY
            when (val tokenResult = chargeTokens(book.userId, tokenCost, "generate summary")) {
                is AppResult.Failure -> return tokenResult
                is AppResult.Success -> Unit
            }
            val summary = contentGenerator.generateBookSummary(book)
            bookDao.updateSummary(bookId, summary)
            AppResult.Success(summary)
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Failed to generate summary", t)
        }
    }

    // ── Chapters ─────────────────────────────────────────────────────

    override fun observeChapters(bookId: Long): Flow<List<Chapter>> {
        return chapterDao.observeChaptersByBook(bookId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addChapter(
        bookId: Long,
        title: String,
        tone: String?,
        setting: String?,
        summary: String,
        desiredWordCount: Int
    ): AppResult<Chapter> {
        return try {
            val nextNum = (chapterDao.getMaxChapterNum(bookId) ?: 0) + 1
            val entity = ChapterEntity(
                bookId = bookId,
                chapterNum = nextNum,
                title = title,
                tone = tone,
                setting = setting,
                summary = summary,
                desiredWordCount = desiredWordCount,
            )
            val id = chapterDao.insert(entity)
            val saved = chapterDao.getChapterById(id)!!
            AppResult.Success(saved.toDomain())
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Failed to add chapter", t)
        }
    }

    override suspend fun autoGenerateChapter(bookId: Long, count: Int): AppResult<List<Chapter>> {
        return try {
            val book = bookDao.getBookById(bookId)
                ?: return AppResult.Failure("Book not found")
            val tokenCost = BookConstants.TOKEN_COST_AUTO_GENERATE_CHAPTERS * count.coerceAtLeast(0)
            when (val tokenResult = chargeTokens(book.userId, tokenCost, "auto-generate chapter metadata")) {
                is AppResult.Failure -> return tokenResult
                is AppResult.Success -> Unit
            }
            val generated = mutableListOf<Chapter>()

            var lastSummary: String? = chapterDao.getChaptersByBook(bookId)
                .lastOrNull()?.let { it.renderedSummary ?: it.summary }

            repeat(count) {
                val existingTitles = chapterDao.getChaptersByBook(bookId)
                    .joinToString(", ") { "'${it.title}'" }
                val charNames = characterDao.getCharactersByBook(bookId)
                    .joinToString(", ") { "'${it.name}'" }

                val details = contentGenerator.generateChapterDetails(
                    book = book,
                    existingChapterTitles = existingTitles,
                    characterNames = charNames,
                    lastChapterSummary = lastSummary,
                ) ?: return AppResult.Failure("AI could not generate chapter details")

                val nextNum = (chapterDao.getMaxChapterNum(bookId) ?: 0) + 1
                val desiredWc = if (book.bookType == BookConstants.CHILDRENS_BOOK) 600 else 5000

                val entity = ChapterEntity(
                    bookId = bookId,
                    chapterNum = nextNum,
                    title = (details["title"] as? String)?.take(40) ?: "Chapter $nextNum",
                    tone = details["tone"] as? String,
                    setting = details["setting"] as? String,
                    summary = (details["summary"] as? String) ?: "",
                    desiredWordCount = desiredWc,
                )
                val id = chapterDao.insert(entity)
                val saved = chapterDao.getChapterById(id)!!.toDomain()
                generated.add(saved)
                lastSummary = saved.summary
            }
            AppResult.Success(generated)
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Failed to auto-generate chapters", t)
        }
    }

    override suspend fun generateChapterSummary(chapterId: Long): AppResult<String> {
        return try {
            val chapter = chapterDao.getChapterById(chapterId)
                ?: return AppResult.Failure("Chapter not found")
            val book = bookDao.getBookById(chapter.bookId)
                ?: return AppResult.Failure("Book not found")
            val tokenCost = BookConstants.TOKEN_COST_GENERATE_SUMMARY
            when (val tokenResult = chargeTokens(book.userId, tokenCost, "generate chapter summary")) {
                is AppResult.Failure -> return tokenResult
                is AppResult.Success -> Unit
            }
            val summary = contentGenerator.generateChapterSummary(book, chapter)
            chapterDao.update(chapter.copy(summary = summary))
            AppResult.Success(summary)
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Failed to generate summary", t)
        }
    }

    override suspend fun renderChapter(bookId: Long, chapterId: Long): AppResult<Chapter> {
        return try {
            val book = bookDao.getBookById(bookId)
                ?: return AppResult.Failure("Book not found")
            val chapter = chapterDao.getChapterById(chapterId)
                ?: return AppResult.Failure("Chapter not found")
            val tokenCost = BookConstants.chapterRenderTokenCost(book.bookType, chapter.desiredWordCount)
            when (val tokenResult = chargeTokens(book.userId, tokenCost, "render chapter")) {
                is AppResult.Failure -> return tokenResult
                is AppResult.Success -> Unit
            }

            bookDao.setRenderingFlag(bookId, true)
            try {
                val renderedText = chapterRenderer.renderChapter(book, chapter)
                val chapterSummary = chapterRenderer.finalizeChapter(book, chapter, renderedText)

                val updated = chapter.copy(
                    rendered = true,
                    renderedContent = renderedText,
                    renderedSummary = chapterSummary,
                    partialRenderedText = null,
                )
                chapterDao.update(updated)

                // Update running summary
                val freshBook = bookDao.getBookById(bookId)!!
                // Running summary is updated inside finalizeChapter via OpenAI

                AppResult.Success(updated.toDomain())
            } finally {
                bookDao.setRenderingFlag(bookId, false)
            }
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Failed to render chapter", t)
        }
    }

    override suspend fun renderAllChapters(bookId: Long): Flow<RenderProgress> = flow {
        val book = bookDao.getBookById(bookId)
            ?: throw IllegalArgumentException("Book not found")
        val unrendered = chapterDao.getUnrenderedChapters(bookId)
        val total = unrendered.size

        if (total == 0) {
            emit(RenderProgress(0, 0, message = "No unrendered chapters."))
            return@flow
        }

        bookDao.setRenderingFlag(bookId, true)
        try {
            unrendered.forEachIndexed { index, chapter ->
                val tokenCost = BookConstants.chapterRenderTokenCost(book.bookType, chapter.desiredWordCount)
                when (val tokenResult = chargeTokens(book.userId, tokenCost, "render chapter")) {
                    is AppResult.Failure -> throw IllegalStateException(tokenResult.message)
                    is AppResult.Success -> Unit
                }

                emit(RenderProgress(index + 1, total, message = "Rendering: ${chapter.title}"))

                val text = chapterRenderer.renderChapter(book, chapter)
                val summary = chapterRenderer.finalizeChapter(book, chapter, text)

                chapterDao.update(
                    chapter.copy(
                        rendered = true,
                        renderedContent = text,
                        renderedSummary = summary,
                        partialRenderedText = null,
                    )
                )
            }
            emit(RenderProgress(total, total, message = "All chapters rendered."))
        } finally {
            bookDao.setRenderingFlag(bookId, false)
        }
    }

    override suspend fun deleteChapter(chapterId: Long): AppResult<Unit> {
        return try {
            chapterDao.deleteChapter(chapterId)
            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Failed to delete chapter", t)
        }
    }

    // ── Characters ───────────────────────────────────────────────────

    override fun observeCharacters(bookId: Long): Flow<List<Character>> {
        return characterDao.observeCharactersByBook(bookId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addCharacter(
        bookId: Long,
        name: String,
        age: Int?,
        bio: String?,
        role: String
    ): AppResult<Character> {
        return try {
            val entity = CharacterEntity(bookId = bookId, name = name, age = age, bio = bio, role = role)
            val id = characterDao.insert(entity)
            val saved = characterDao.getCharacterById(id)!!
            AppResult.Success(saved.toDomain())
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Failed to add character", t)
        }
    }

    override suspend fun autoGenerateCharacter(bookId: Long): AppResult<Character> {
        return try {
            val book = bookDao.getBookById(bookId)
                ?: return AppResult.Failure("Book not found")
            val tokenCost = BookConstants.TOKEN_COST_GENERATE_CHARACTER
            when (val tokenResult = chargeTokens(book.userId, tokenCost, "generate character")) {
                is AppResult.Failure -> return tokenResult
                is AppResult.Success -> Unit
            }
            val entity = contentGenerator.generateCharacter(book)
                ?: return AppResult.Failure("AI could not generate a character")
            val id = characterDao.insert(entity)
            val saved = characterDao.getCharacterById(id)!!
            AppResult.Success(saved.toDomain())
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Failed to auto-generate character", t)
        }
    }

    override suspend fun deleteCharacter(characterId: Long): AppResult<Unit> {
        return try {
            characterDao.deleteCharacter(characterId)
            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Failed to delete character", t)
        }
    }

    // ── Cover Art ────────────────────────────────────────────────────

    override suspend fun generateCoverArt(bookId: Long, userDescription: String?): AppResult<String> {
        return try {
            val book = bookDao.getBookById(bookId)
                ?: return AppResult.Failure("Book not found")
            val tokenCost = if (book.tokenChargeForImage > 0) book.tokenChargeForImage else BookConstants.TOKEN_COST_COVER_ART
            when (val tokenResult = chargeTokens(book.userId, tokenCost, "generate cover art")) {
                is AppResult.Failure -> return tokenResult
                is AppResult.Success -> Unit
            }
            val b64 = contentGenerator.generateCoverArt(book, userDescription)
                ?: return AppResult.Failure("Cover art generation failed")
            AppResult.Success(b64)
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Failed to generate cover art", t)
        }
    }

    override suspend fun saveCoverArt(bookId: Long, base64Data: String): AppResult<String> {
        return try {
            val bytes = Base64.decode(base64Data, Base64.DEFAULT)
            val dir = File(context.filesDir, "cover_art")
            dir.mkdirs()
            val file = File(dir, "book_${bookId}.jpg")

            // Compress and save
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            FileOutputStream(file).use { out ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, out)
            }

            bookDao.setCoverArtPath(bookId, file.absolutePath)
            AppResult.Success(file.absolutePath)
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Failed to save cover art", t)
        }
    }

    override suspend fun removeCoverArt(bookId: Long): AppResult<Unit> {
        return try {
            val book = bookDao.getBookById(bookId) ?: return AppResult.Failure("Book not found")
            book.coverArtPath?.let { File(it).delete() }
            bookDao.setCoverArtPath(bookId, null)
            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Failed to remove cover art", t)
        }
    }

    // ── Mappers ──────────────────────────────────────────────────────

    private fun BookEntity.toDomain(): Book = Book(
        id = id,
        userId = userId,
        title = title,
        author = author,
        bookType = bookType,
        genre = genre,
        language = language,
        pov = pov,
        writingStyle = writingStyle,
        summary = summary,
        runningSummary = runningSummary,
        coverArtPath = coverArtPath,
        isRenderingChapters = isRenderingChapters,
        isGeneratingCoverArt = isGeneratingCoverArt,
        tokenChargeForImage = tokenChargeForImage,
        createdAtIso = createdAtIso,
    )

    private fun ChapterEntity.toDomain(): Chapter = Chapter(
        id = id,
        bookId = bookId,
        chapterNum = chapterNum,
        title = title,
        setting = setting,
        summary = summary,
        tone = tone,
        desiredWordCount = desiredWordCount,
        rendered = rendered,
        renderedContent = renderedContent,
        renderedSummary = renderedSummary,
        partialRenderedText = partialRenderedText,
        currentSegment = currentSegment,
    )

    private fun CharacterEntity.toDomain(): Character = Character(
        id = id,
        bookId = bookId,
        name = name,
        age = age,
        bio = bio,
        role = role,
    )

    private suspend fun chargeTokens(userId: Long, amount: Int, action: String): AppResult<Unit> {
        if (amount <= 0) return AppResult.Success(Unit)

        val balance = userDao.getTokenBalance(userId)
            ?: return AppResult.Failure("User not found")

        if (balance < amount) {
            return AppResult.Failure("Not enough tokens to $action. Need $amount, have $balance.")
        }

        val affectedRows = userDao.subtractTokens(userId, amount)
        return if (affectedRows > 0) {
            AppResult.Success(Unit)
        } else {
            val latestBalance = userDao.getTokenBalance(userId) ?: 0
            AppResult.Failure("Not enough tokens to $action. Need $amount, have $latestBalance.")
        }
    }
}
