package com.monyechi.aistorysculptor.domain.repository

import com.monyechi.aistorysculptor.domain.common.AppResult
import com.monyechi.aistorysculptor.domain.model.UserProfile

interface AuthRepository {
    suspend fun register(username: String, email: String, password: String, displayName: String): AppResult<UserProfile>
    suspend fun login(email: String, password: String): AppResult<UserProfile>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
    suspend fun getCurrentUser(): UserProfile?
    suspend fun getTokenBalance(): Int
    suspend fun addTokens(amount: Int): AppResult<Unit>
    suspend fun subtractTokens(amount: Int): AppResult<Unit>
    suspend fun deleteAccount(): AppResult<Unit>
}
