package com.monyechi.aistorysculptor.data.api

import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit interface for the OpenAI REST API.
 * Base URL: https://api.openai.com/v1/
 * Auth header added via [OpenAiInterceptor].
 */
interface OpenAiApi {

    /** Chat completions — used for text generation (summaries, chapters, characters). */
    @POST("chat/completions")
    suspend fun chatCompletion(@Body request: ChatCompletionRequest): ChatCompletionResponse

    /** Image generation — used for cover art (gpt-image-1). */
    @POST("images/generations")
    suspend fun generateImage(@Body request: ImageGenerationRequest): ImageGenerationResponse
}
