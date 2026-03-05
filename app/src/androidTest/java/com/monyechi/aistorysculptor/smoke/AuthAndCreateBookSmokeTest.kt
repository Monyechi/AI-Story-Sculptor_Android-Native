package com.monyechi.aistorysculptor.smoke

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monyechi.aistorysculptor.domain.common.AppResult
import com.monyechi.aistorysculptor.domain.model.Book
import com.monyechi.aistorysculptor.domain.model.UserProfile
import com.monyechi.aistorysculptor.domain.usecase.CreateBookUseCase
import com.monyechi.aistorysculptor.domain.usecase.GetCurrentUserUseCase
import com.monyechi.aistorysculptor.domain.usecase.LoginUseCase
import com.monyechi.aistorysculptor.domain.usecase.RegisterUseCase
import com.monyechi.aistorysculptor.ui.common.UiState
import com.monyechi.aistorysculptor.ui.viewmodel.AuthViewModel
import com.monyechi.aistorysculptor.ui.viewmodel.CreateBookViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class AuthAndCreateBookSmokeTest {

    @Test
    fun login_register_and_create_book_happy_path() = runTest {
        val authRepo = FakeAuthRepository()
        val bookRepo = FakeBookRepository()

        val user = UserProfile(id = 101, username = "smoke", email = "smoke@mail.com")
        authRepo.registerResult = AppResult.Success(user)
        authRepo.loginResult = AppResult.Success(user)
        bookRepo.createBookResult = AppResult.Success(Book(id = 501, userId = 101, title = "Smoke Book"))

        val authViewModel = withContext(Dispatchers.Main) {
            AuthViewModel(LoginUseCase(authRepo), RegisterUseCase(authRepo))
        }

        withContext(Dispatchers.Main) {
            authViewModel.register("smoke", user.email, "password", "Smoke User") {}
        }
        assertEquals(UiState.Success(Unit), authViewModel.authState.value)

        withContext(Dispatchers.Main) {
            authViewModel.login(user.email, "password") { authRepo.currentUser = user }
        }
        assertEquals(UiState.Success(Unit), authViewModel.authState.value)

        val createBookViewModel = withContext(Dispatchers.Main) {
            CreateBookViewModel(CreateBookUseCase(bookRepo), GetCurrentUserUseCase(authRepo))
        }

        withContext(Dispatchers.Main) {
            createBookViewModel.updateTitle("Smoke Book")
            createBookViewModel.updateGenre("Drama")
            createBookViewModel.submit {}
        }

        assertEquals(101L, bookRepo.lastCreate?.first)
        assertEquals(UiState.Success(Book(id = 501, userId = 101, title = "Smoke Book")), createBookViewModel.createState.value)
    }
}
