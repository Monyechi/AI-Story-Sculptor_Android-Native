package com.monyechi.aistorysculptor.data.api

import com.monyechi.aistorysculptor.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Adds the `Authorization: Bearer <OPENAI_API_KEY>` header to every
 * request made through the OpenAI Retrofit instance.
 */
@Singleton
class OpenAiInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            .addHeader("Content-Type", "application/json")
            .build()
        return chain.proceed(request)
    }
}
