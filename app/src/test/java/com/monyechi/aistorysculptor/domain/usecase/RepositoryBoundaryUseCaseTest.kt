package com.monyechi.aistorysculptor.domain.usecase

import com.monyechi.aistorysculptor.domain.common.AppResult
import com.monyechi.aistorysculptor.domain.model.Book
import com.monyechi.aistorysculptor.domain.model.CreateBookRequest
import com.monyechi.aistorysculptor.domain.model.UserProfile
import com.monyechi.aistorysculptor.fakes.FakeAuthRepository
import com.monyechi.aistorysculptor.fakes.FakeBookRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RepositoryBoundaryUseCaseTest {

    @Test
    fun `login use case forwards args to auth repository`() = runTest {
        val repo = FakeAuthRepository().apply {
            loginResult = AppResult.Success(UserProfile(3, "ana", "ana@mail.com"))
        }

        val result = LoginUseCase(repo)("ana@mail.com", "secret")

        assertEquals("ana@mail.com" to "secret", repo.lastLogin)
        assertEquals(AppResult.Success(UserProfile(3, "ana", "ana@mail.com")), result)
    }

    @Test
    fun `create book use case forwards user and payload to repository`() = runTest {
        val repo = FakeBookRepository().apply {
            createBookResult = AppResult.Success(Book(id = 99, userId = 42, title = "Boundary"))
        }
        val request = CreateBookRequest(
            title = "Boundary",
            author = "Dev",
            bookType = "fiction-novel",
            genre = "Sci-Fi",
            language = "English",
            pov = "First Person",
            writingStyle = "descriptive",
            summary = "A boundary test",
        )

        val result = CreateBookUseCase(repo)(42L, request)

        assertEquals(42L to request, repo.lastCreate)
        assertEquals(AppResult.Success(Book(id = 99, userId = 42, title = "Boundary")), result)
    }
}
