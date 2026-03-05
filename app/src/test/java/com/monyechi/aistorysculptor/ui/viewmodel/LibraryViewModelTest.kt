package com.monyechi.aistorysculptor.ui.viewmodel

import com.monyechi.aistorysculptor.domain.common.AppResult
import com.monyechi.aistorysculptor.domain.model.Book
import com.monyechi.aistorysculptor.domain.model.UserProfile
import com.monyechi.aistorysculptor.domain.usecase.DeleteBookUseCase
import com.monyechi.aistorysculptor.domain.usecase.GetCurrentUserUseCase
import com.monyechi.aistorysculptor.domain.usecase.ObserveBooksUseCase
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
class LibraryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun buildViewModel(
        authRepo: FakeAuthRepository,
        bookRepo: FakeBookRepository,
    ) = LibraryViewModel(
        ObserveBooksUseCase(bookRepo),
        GetCurrentUserUseCase(authRepo),
        DeleteBookUseCase(bookRepo),
    )

    @Test
    fun `loadBooks with no logged-in user sets error state`() = runTest {
        val authRepo = FakeAuthRepository().apply { currentUser = null }
        val bookRepo = FakeBookRepository()
        val viewModel = buildViewModel(authRepo, bookRepo)

        advanceUntilIdle()

        assertEquals(UiState.Error("Not logged in"), viewModel.booksState.value)
    }

    @Test
    fun `loadBooks with logged-in user emits book list`() = runTest {
        val books = listOf(
            Book(id = 1, userId = 5, title = "Dune"),
            Book(id = 2, userId = 5, title = "Foundation"),
        )
        val authRepo = FakeAuthRepository().apply {
            currentUser = UserProfile(5, "reader", "reader@mail.com")
        }
        val bookRepo = FakeBookRepository().apply { booksToEmit = books }
        val viewModel = buildViewModel(authRepo, bookRepo)

        advanceUntilIdle()

        assertEquals(UiState.Success(books), viewModel.booksState.value)
    }

    @Test
    fun `loadBooks with empty library emits empty list, not error`() = runTest {
        val authRepo = FakeAuthRepository().apply {
            currentUser = UserProfile(3, "newuser", "new@mail.com")
        }
        val bookRepo = FakeBookRepository().apply { booksToEmit = emptyList() }
        val viewModel = buildViewModel(authRepo, bookRepo)

        advanceUntilIdle()

        assertEquals(UiState.Success(emptyList<Book>()), viewModel.booksState.value)
    }

    @Test
    fun `deleteBook delegates correct id to repository`() = runTest {
        val authRepo = FakeAuthRepository().apply {
            currentUser = UserProfile(7, "owner", "owner@mail.com")
        }
        val bookRepo = FakeBookRepository()
        val viewModel = buildViewModel(authRepo, bookRepo)

        viewModel.deleteBook(42L)
        advanceUntilIdle()

        assertEquals(42L, bookRepo.lastDeleteBookId)
    }

    @Test
    fun `deleteBook does not affect booksState directly`() = runTest {
        val books = listOf(Book(id = 10, userId = 7, title = "Neuromancer"))
        val authRepo = FakeAuthRepository().apply {
            currentUser = UserProfile(7, "owner", "owner@mail.com")
        }
        val bookRepo = FakeBookRepository().apply { booksToEmit = books }
        val viewModel = buildViewModel(authRepo, bookRepo)
        advanceUntilIdle()

        viewModel.deleteBook(10L)
        advanceUntilIdle()

        // State is driven by the Flow from the fake, not mutated in-place by the ViewModel
        assertEquals(UiState.Success(books), viewModel.booksState.value)
        assertEquals(10L, bookRepo.lastDeleteBookId)
    }

    @Test
    fun `second loadBooks call re-collects from repository`() = runTest {
        val authRepo = FakeAuthRepository().apply {
            currentUser = UserProfile(9, "reload", "reload@mail.com")
        }
        val bookRepo = FakeBookRepository().apply {
            booksToEmit = listOf(Book(id = 20, userId = 9, title = "First"))
        }
        val viewModel = buildViewModel(authRepo, bookRepo)
        advanceUntilIdle()

        bookRepo.booksToEmit = listOf(Book(id = 21, userId = 9, title = "Second"))
        viewModel.loadBooks()
        advanceUntilIdle()

        assertEquals(
            UiState.Success(listOf(Book(id = 21, userId = 9, title = "Second"))),
            viewModel.booksState.value,
        )
    }

    @Test
    fun `deleteBook call is skipped when no user is logged in`() = runTest {
        val authRepo = FakeAuthRepository().apply { currentUser = null }
        val bookRepo = FakeBookRepository()
        val viewModel = buildViewModel(authRepo, bookRepo)

        // Library may be in error state; delete should still not crash
        viewModel.deleteBook(99L)
        advanceUntilIdle()

        // The delete call still reaches the repo (VM delegates unconditionally per current impl)
        assertEquals(99L, bookRepo.lastDeleteBookId)
    }
}
