package com.monyechi.aistorysculptor.data.api

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Adds mobile client headers for backend AI proxy requests.
 *
 * The backend owns provider credentials (OpenAI API key, model routing, etc.),
 * so the Android app must not send provider authorization headers.
 */
@Singleton
class OpenAiInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .build()
        return chain.proceed(request)
    }
}
