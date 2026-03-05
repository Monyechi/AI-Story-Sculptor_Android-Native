package com.monyechi.aistorysculptor.ui.viewmodel

import com.monyechi.aistorysculptor.domain.common.AppResult
import com.monyechi.aistorysculptor.domain.model.UserProfile
import com.monyechi.aistorysculptor.domain.usecase.LoginUseCase
import com.monyechi.aistorysculptor.domain.usecase.RegisterUseCase
import com.monyechi.aistorysculptor.fakes.FakeAuthRepository
import com.monyechi.aistorysculptor.testutil.MainDispatcherRule
import com.monyechi.aistorysculptor.ui.common.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `login success updates state and triggers callback`() = runTest {
        val repo = FakeAuthRepository().apply {
            loginResult = AppResult.Success(UserProfile(1, "sam", "sam@mail.com"))
        }
        val viewModel = AuthViewModel(LoginUseCase(repo), RegisterUseCase(repo))
        var callbackCount = 0

        val collected = async { viewModel.authState.take(2).toList() }
        viewModel.login("sam@mail.com", "pw") { callbackCount++ }
        advanceUntilIdle()

        val states = collected.await()
        assertEquals(UiState.Success(Unit), states[0])
        assertEquals(UiState.Loading, states[1])
        assertEquals(UiState.Success(Unit), viewModel.authState.value)
        assertEquals(1, callbackCount)
        assertEquals("sam@mail.com" to "pw", repo.lastLogin)
    }

    @Test
    fun `login failure updates error and skips callback`() = runTest {
        val repo = FakeAuthRepository().apply {
            loginResult = AppResult.Failure("Invalid credentials")
        }
        val viewModel = AuthViewModel(LoginUseCase(repo), RegisterUseCase(repo))
        var called = false

        viewModel.login("sam@mail.com", "bad") { called = true }
        advanceUntilIdle()

        assertEquals(UiState.Error("Invalid credentials"), viewModel.authState.value)
        assertFalse(called)
    }

    @Test
    fun `register success sets success and callback`() = runTest {
        val repo = FakeAuthRepository().apply {
            registerResult = AppResult.Success(UserProfile(7, "new", "new@mail.com"))
        }
        val viewModel = AuthViewModel(LoginUseCase(repo), RegisterUseCase(repo))
        var called = false

        viewModel.register("new", "new@mail.com", "pw", "New User") { called = true }
        advanceUntilIdle()

        assertEquals(UiState.Success(Unit), viewModel.authState.value)
        assertTrue(called)
        assertEquals(
            FakeAuthRepository.RegisterCall("new", "new@mail.com", "pw", "New User"),
            repo.lastRegister,
        )
    }

    @Test
    fun `clearError resets auth state to success`() = runTest {
        val repo = FakeAuthRepository().apply {
            loginResult = AppResult.Failure("boom")
        }
        val viewModel = AuthViewModel(LoginUseCase(repo), RegisterUseCase(repo))

        viewModel.login("a", "b") {}
        advanceUntilIdle()
        viewModel.clearError()

        assertEquals(UiState.Success(Unit), viewModel.authState.value)
    }
}
