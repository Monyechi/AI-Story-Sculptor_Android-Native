package com.monyechi.aistorysculptor.domain.repository

import com.monyechi.aistorysculptor.domain.common.AppResult

interface AuthRepository {
    suspend fun login(email: String, password: String): AppResult<Unit>
    suspend fun register(email: String, password: String, displayName: String): AppResult<Unit>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}
