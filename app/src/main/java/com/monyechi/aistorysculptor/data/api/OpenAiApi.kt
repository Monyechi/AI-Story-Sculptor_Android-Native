package com.monyechi.aistorysculptor.data.api

import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit interface for backend-owned AI proxy endpoints.
 *
 * Base URL: backend API root (`BuildConfig.BASE_URL`).
 * The backend handles provider authentication and request forwarding.
 */
interface OpenAiApi {

    /** Chat completions proxy — used for text generation (summaries, chapters, characters). */
    @POST("api/v1/mobile/ai/chat/completions/")
    suspend fun chatCompletion(@Body request: ChatCompletionRequest): ChatCompletionResponse

    /** Image generation proxy — used for cover art generation. */
    @POST("api/v1/mobile/ai/images/generations/")
    suspend fun generateImage(@Body request: ImageGenerationRequest): ImageGenerationResponse
}
