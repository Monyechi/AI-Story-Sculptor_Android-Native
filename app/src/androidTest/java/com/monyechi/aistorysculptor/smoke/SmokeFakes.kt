package com.monyechi.aistorysculptor.smoke

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
    var loginResult: AppResult<UserProfile> = AppResult.Failure("unset")
    var registerResult: AppResult<UserProfile> = AppResult.Failure("unset")
    var currentUser: UserProfile? = null

    override suspend fun register(username: String, email: String, password: String, displayName: String) = registerResult
    override suspend fun login(email: String, password: String) = loginResult
    override suspend fun logout() = Unit
    override suspend fun isLoggedIn(): Boolean = currentUser != null
    override suspend fun getCurrentUser(): UserProfile? = currentUser
    override suspend fun getTokenBalance(): Int = 0
    override suspend fun addTokens(amount: Int): AppResult<Unit> = AppResult.Success(Unit)
    override suspend fun subtractTokens(amount: Int): AppResult<Unit> = AppResult.Success(Unit)
    override suspend fun deleteAccount(): AppResult<Unit> = AppResult.Success(Unit)
}

class FakeBookRepository : BookRepository {
    var createBookResult: AppResult<Book> = AppResult.Failure("unset")
    var lastCreate: Pair<Long, CreateBookRequest>? = null

    override fun observeBooks(userId: Long): Flow<List<Book>> = flowOf(emptyList())
    override suspend fun createBook(userId: Long, request: CreateBookRequest): AppResult<Book> {
        lastCreate = userId to request
        return createBookResult
    }
    override suspend fun getBookDetails(bookId: Long): AppResult<BookDetails> = AppResult.Failure("n/a")
    override suspend fun deleteBook(bookId: Long): AppResult<Unit> = AppResult.Success(Unit)
    override suspend fun generateBookSummary(bookId: Long): AppResult<String> = AppResult.Failure("n/a")
    override fun observeChapters(bookId: Long): Flow<List<Chapter>> = flowOf(emptyList())
    override suspend fun addChapter(bookId: Long, title: String, tone: String?, setting: String?, summary: String, desiredWordCount: Int): AppResult<Chapter> = AppResult.Failure("n/a")
    override suspend fun autoGenerateChapter(bookId: Long, count: Int): AppResult<List<Chapter>> = AppResult.Failure("n/a")
    override suspend fun generateChapterSummary(chapterId: Long): AppResult<String> = AppResult.Failure("n/a")
    override suspend fun renderChapter(bookId: Long, chapterId: Long): AppResult<Chapter> = AppResult.Failure("n/a")
    override suspend fun renderAllChapters(bookId: Long): Flow<RenderProgress> = flowOf()
    override suspend fun deleteChapter(chapterId: Long): AppResult<Unit> = AppResult.Success(Unit)
    override fun observeCharacters(bookId: Long): Flow<List<Character>> = flowOf(emptyList())
    override suspend fun addCharacter(bookId: Long, name: String, age: Int?, bio: String?, role: String): AppResult<Character> = AppResult.Failure("n/a")
    override suspend fun autoGenerateCharacter(bookId: Long): AppResult<Character> = AppResult.Failure("n/a")
    override suspend fun deleteCharacter(characterId: Long): AppResult<Unit> = AppResult.Success(Unit)
    override suspend fun generateCoverArt(bookId: Long, userDescription: String?): AppResult<String> = AppResult.Failure("n/a")
    override suspend fun saveCoverArt(bookId: Long, base64Data: String): AppResult<String> = AppResult.Failure("n/a")
    override suspend fun removeCoverArt(bookId: Long): AppResult<Unit> = AppResult.Success(Unit)
}
