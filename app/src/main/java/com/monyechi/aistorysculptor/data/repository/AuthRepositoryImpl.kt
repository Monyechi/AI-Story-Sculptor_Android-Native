package com.monyechi.aistorysculptor.data.repository

import com.monyechi.aistorysculptor.data.api.AuthApi
import com.monyechi.aistorysculptor.data.api.LoginRequestDto
import com.monyechi.aistorysculptor.data.api.RegisterRequestDto
import com.monyechi.aistorysculptor.data.datastore.TokenStorage
import com.monyechi.aistorysculptor.domain.common.AppResult
import com.monyechi.aistorysculptor.domain.repository.AuthRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun login(email: String, password: String): AppResult<Unit> {
        return try {
            val response = authApi.login(LoginRequestDto(email = email, password = password))
            tokenStorage.saveTokens(response.accessToken, response.refreshToken)
            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Failure(message = t.message ?: "Login failed", throwable = t)
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        displayName: String
    ): AppResult<Unit> {
        return try {
            val response = authApi.register(
                RegisterRequestDto(email = email, password = password, displayName = displayName)
            )
            tokenStorage.saveTokens(response.accessToken, response.refreshToken)
            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Failure(message = t.message ?: "Registration failed", throwable = t)
        }
    }

    override suspend fun logout() {
        tokenStorage.clearTokens()
    }

    override suspend fun isLoggedIn(): Boolean {
        return !tokenStorage.accessToken.firstOrNull().isNullOrBlank()
    }
}
