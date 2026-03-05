package com.monyechi.aistorysculptor.ui.viewmodel

import com.monyechi.aistorysculptor.domain.common.AppResult
import com.monyechi.aistorysculptor.domain.model.Book
import com.monyechi.aistorysculptor.domain.model.UserProfile
import com.monyechi.aistorysculptor.domain.usecase.CreateBookUseCase
import com.monyechi.aistorysculptor.domain.usecase.GetCurrentUserUseCase
import com.monyechi.aistorysculptor.fakes.FakeAuthRepository
import com.monyechi.aistorysculptor.fakes.FakeBookRepository
import com.monyechi.aistorysculptor.testutil.MainDispatcherRule
import com.monyechi.aistorysculptor.ui.common.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateBookViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `submit without logged in user returns not logged in error`() = runTest {
        val authRepo = FakeAuthRepository().apply { currentUser = null }
        val bookRepo = FakeBookRepository()
        val viewModel = CreateBookViewModel(CreateBookUseCase(bookRepo), GetCurrentUserUseCase(authRepo))

        viewModel.submit {}
        advanceUntilIdle()

        assertEquals(UiState.Error("Not logged in"), viewModel.createState.value)
    }

    @Test
    fun `submit with missing title fails validation before repository call`() = runTest {
        val authRepo = FakeAuthRepository().apply {
            currentUser = UserProfile(10, "sarah", "sarah@mail.com")
        }
        val bookRepo = FakeBookRepository()
        val viewModel = CreateBookViewModel(CreateBookUseCase(bookRepo), GetCurrentUserUseCase(authRepo))

        viewModel.updateGenre("Fantasy")
        viewModel.submit {}
        advanceUntilIdle()

        assertEquals(UiState.Error("Title is required."), viewModel.createState.value)
        assertNull(bookRepo.lastCreate)
    }

    @Test
    fun `submit success sends request to repository and invokes callback`() = runTest {
        val authRepo = FakeAuthRepository().apply {
            currentUser = UserProfile(11, "mike", "mike@mail.com")
        }
        val createdBook = Book(id = 45, userId = 11, title = "Skyfall")
        val bookRepo = FakeBookRepository().apply {
            createBookResult = AppResult.Success(createdBook)
        }
        val viewModel = CreateBookViewModel(CreateBookUseCase(bookRepo), GetCurrentUserUseCase(authRepo))
        var createdId: Long? = null

        viewModel.updateTitle("Skyfall")
        viewModel.updateGenre("Adventure")
        viewModel.submit { createdId = it }
        advanceUntilIdle()

        assertEquals(UiState.Success(createdBook), viewModel.createState.value)
        assertEquals(45L, createdId)
        assertEquals(11L, bookRepo.lastCreate?.first)
        assertEquals("Skyfall", bookRepo.lastCreate?.second?.title)
        assertEquals("Adventure", bookRepo.lastCreate?.second?.genre)
    }

    @Test
    fun `generateSummary enforces required metadata`() = runTest {
        val authRepo = FakeAuthRepository()
        val bookRepo = FakeBookRepository()
        val viewModel = CreateBookViewModel(CreateBookUseCase(bookRepo), GetCurrentUserUseCase(authRepo))

        viewModel.generateSummary()
        advanceUntilIdle()

        assertEquals(UiState.Error("Title is required."), viewModel.summaryState.value)

        viewModel.updateTitle("Book")
        viewModel.generateSummary()
        advanceUntilIdle()

        assertEquals(UiState.Error("Genre is required."), viewModel.summaryState.value)
    }
}
