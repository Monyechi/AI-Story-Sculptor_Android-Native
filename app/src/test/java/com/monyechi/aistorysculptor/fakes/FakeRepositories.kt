package com.monyechi.aistorysculptor.fakes

import com.monyechi.aistorysculptor.domain.common.AppResult
import com.monyechi.aistorysculptor.domain.model.Book
import com.monyechi.aistorysculptor.domain.model.BookDetails
import com.monyechi.aistorysculptor.domain.model.Chapter
import com.monyechi.aistorysculptor.domain.model.Character
import com.monyechi.aistorysculptor.domain.model.CreateBookRequest
import com.monyechi.aistorysculptor.domain.model.RenderProgress
import com.monyechi.aistorysculptor.domain.model.UserProfile
import com.monyechi.aistorysculptor.domain.repository.AuthRepository
import com.monyechi.aistorysculptor.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeAuthRepository : AuthRepository {
    var loginResult: AppResult<UserProfile> = AppResult.Failure("login not configured")
    var registerResult: AppResult<UserProfile> = AppResult.Failure("register not configured")
    var currentUser: UserProfile? = null

    var lastLogin: Pair<String, String>? = null
    var lastRegister: RegisterCall? = null

    data class RegisterCall(
        val username: String,
        val email: String,
        val password: String,
        val displayName: String,
    )

    override suspend fun register(
        username: String,
        email: String,
        password: String,
        displayName: String,
    ): AppResult<UserProfile> {
        lastRegister = RegisterCall(username, email, password, displayName)
        return registerResult
    }

    override suspend fun login(email: String, password: String): AppResult<UserProfile> {
        lastLogin = email to password
        return loginResult
    }

    override suspend fun logout() = Unit

    override suspend fun isLoggedIn(): Boolean = currentUser != null

    override suspend fun getCurrentUser(): UserProfile? = currentUser

    override suspend fun getTokenBalance(): Int = currentUser?.tokens ?: 0

    override suspend fun addTokens(amount: Int): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun subtractTokens(amount: Int): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun deleteAccount(): AppResult<Unit> = AppResult.Success(Unit)
}

class FakeBookRepository : BookRepository {
    var createBookResult: AppResult<Book> = AppResult.Failure("create not configured")
    var lastCreate: Pair<Long, CreateBookRequest>? = null

    var booksToEmit: List<Book> = emptyList()
    var bookDetailsResult: AppResult<BookDetails> = AppResult.Failure("details not configured")
    var deleteBookResult: AppResult<Unit> = AppResult.Success(Unit)
    var lastDeleteBookId: Long? = null

    override fun observeBooks(userId: Long): Flow<List<Book>> = flowOf(booksToEmit)

    override suspend fun createBook(userId: Long, request: CreateBookRequest): AppResult<Book> {
        lastCreate = userId to request
        return createBookResult
    }

    override suspend fun getBookDetails(bookId: Long): AppResult<BookDetails> = bookDetailsResult

    override suspend fun deleteBook(bookId: Long): AppResult<Unit> {
        lastDeleteBookId = bookId
        return deleteBookResult
    }

    override suspend fun generateBookSummary(bookId: Long): AppResult<String> =
        AppResult.Failure("not needed")

    override fun observeChapters(bookId: Long): Flow<List<Chapter>> = flowOf(emptyList())

    override suspend fun addChapter(
        bookId: Long,
        title: String,
        tone: String?,
        setting: String?,
        summary: String,
        desiredWordCount: Int,
    ): AppResult<Chapter> = AppResult.Failure("not needed")

    override suspend fun autoGenerateChapter(bookId: Long, count: Int): AppResult<List<Chapter>> =
        AppResult.Failure("not needed")

    override suspend fun generateChapterSummary(chapterId: Long): AppResult<String> =
        AppResult.Failure("not needed")

    override suspend fun renderChapter(bookId: Long, chapterId: Long): AppResult<Chapter> =
        AppResult.Failure("not needed")

    override suspend fun renderAllChapters(bookId: Long): Flow<RenderProgress> = flowOf()

    override suspend fun deleteChapter(chapterId: Long): AppResult<Unit> = AppResult.Success(Unit)

    override fun observeCharacters(bookId: Long): Flow<List<Character>> = flowOf(emptyList())

    override suspend fun addCharacter(
        bookId: Long,
        name: String,
        age: Int?,
        bio: String?,
        role: String,
    ): AppResult<Character> = AppResult.Failure("not needed")

    override suspend fun autoGenerateCharacter(bookId: Long): AppResult<Character> =
        AppResult.Failure("not needed")

    override suspend fun deleteCharacter(characterId: Long): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun generateCoverArt(bookId: Long, userDescription: String?): AppResult<String> =
        AppResult.Failure("not needed")

    override suspend fun saveCoverArt(bookId: Long, base64Data: String): AppResult<String> =
        AppResult.Failure("not needed")

    override suspend fun removeCoverArt(bookId: Long): AppResult<Unit> = AppResult.Success(Unit)
}
