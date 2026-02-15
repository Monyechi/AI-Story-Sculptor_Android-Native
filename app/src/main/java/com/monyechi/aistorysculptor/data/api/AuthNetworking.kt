package com.monyechi.aistorysculptor.data.api

import com.monyechi.aistorysculptor.data.datastore.TokenStorage
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessTokenInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenStorage.accessToken.firstOrNull() }
        val request = if (!token.isNullOrBlank()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val authApi: AuthApi
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        val refreshedAccessToken = runBlocking {
            val refreshToken = tokenStorage.refreshToken.firstOrNull() ?: return@runBlocking null
            try {
                val refreshResponse = authApi.refreshToken(
                    RefreshTokenRequestDto(refreshToken = refreshToken)
                )
                tokenStorage.saveTokens(
                    accessToken = refreshResponse.accessToken,
                    refreshToken = refreshResponse.refreshToken
                )
                refreshResponse.accessToken
            } catch (_: Exception) {
                tokenStorage.clearTokens()
                null
            }
        }

        return refreshedAccessToken?.let {
            response.request.newBuilder()
                .header("Authorization", "Bearer $it")
                .build()
        }
    }

    private fun responseCount(response: Response): Int {
        var current: Response? = response
        var result = 1
        while (current?.priorResponse != null) {
            result++
            current = current.priorResponse
        }
        return result
    }
}
