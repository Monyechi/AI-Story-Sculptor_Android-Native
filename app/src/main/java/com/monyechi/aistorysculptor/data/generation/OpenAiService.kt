package com.monyechi.aistorysculptor.data.generation

import com.monyechi.aistorysculptor.BuildConfig
import com.monyechi.aistorysculptor.data.api.ChatCompletionRequest
import com.monyechi.aistorysculptor.data.api.ChatMessage
import com.monyechi.aistorysculptor.data.api.ImageGenerationRequest
import com.monyechi.aistorysculptor.data.api.OpenAiApi
import com.monyechi.aistorysculptor.data.api.ResponseFormat
import com.monyechi.aistorysculptor.domain.model.BookConstants
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thin wrapper around [OpenAiApi] that provides high-level helpers
 * matching Book_Assistant's OpenAI usage patterns.
 */
@Singleton
class OpenAiService @Inject constructor(
    private val api: OpenAiApi,
) {
    private val model: String = BuildConfig.OPENAI_MODEL

    // ── Text generation ─────────────────────────────────────────────────

    /** Simple chat completion (no structured output). */
    suspend fun chatCompletion(
        messages: List<ChatMessage>,
        maxTokens: Int = 500,
        temperature: Double = BookConstants.DEFAULT_TEMPERATURE,
        topP: Double = BookConstants.DEFAULT_TOP_P,
        frequencyPenalty: Double? = null,
        presencePenalty: Double? = null,
    ): String {
        val response = api.chatCompletion(
            ChatCompletionRequest(
                model = model,
                messages = messages,
                maxTokens = maxTokens,
                temperature = temperature,
                topP = topP,
                frequencyPenalty = frequencyPenalty,
                presencePenalty = presencePenalty,
            )
        )
        return extractContent(response.choices.firstOrNull()?.message?.content)
    }

    /** Chat completion expecting JSON output. */
    suspend fun chatCompletionJson(
        messages: List<ChatMessage>,
        maxTokens: Int = 500,
        temperature: Double? = null,
        topP: Double? = null,
    ): String {
        val response = api.chatCompletion(
            ChatCompletionRequest(
                model = model,
                messages = messages,
                maxTokens = maxTokens,
                temperature = temperature,
                topP = topP,
                responseFormat = ResponseFormat(type = "json_object"),
            )
        )
        return extractContent(response.choices.firstOrNull()?.message?.content)
    }

    // ── Image generation ────────────────────────────────────────────────

    /** Generate a cover art image; returns base64-encoded image data. */
    suspend fun generateImage(
        prompt: String,
        size: String = "1024x1536",
        quality: String = "high",
    ): String? {
        val response = api.generateImage(
            ImageGenerationRequest(
                model = "gpt-image-1",
                prompt = prompt,
                size = size,
                quality = quality,
            )
        )
        return response.data.firstOrNull()?.b64Json
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private fun extractContent(raw: String?): String = raw?.trim() ?: ""

    /** Rough token estimate: ~4 chars per token on average. */
    fun estimateTokens(text: String): Int = text.length / 4
}
