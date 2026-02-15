package com.monyechi.aistorysculptor.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

// ── Chat Completions ────────────────────────────────────────────────────────

@Serializable
data class ChatCompletionRequest(
    @SerialName("model") val model: String,
    @SerialName("messages") val messages: List<ChatMessage>,
    @SerialName("max_tokens") val maxTokens: Int? = null,
    @SerialName("temperature") val temperature: Double? = null,
    @SerialName("top_p") val topP: Double? = null,
    @SerialName("frequency_penalty") val frequencyPenalty: Double? = null,
    @SerialName("presence_penalty") val presencePenalty: Double? = null,
    @SerialName("response_format") val responseFormat: ResponseFormat? = null,
)

@Serializable
data class ChatMessage(
    @SerialName("role") val role: String,       // "system", "user", "assistant"
    @SerialName("content") val content: String,
)

@Serializable
data class ResponseFormat(
    @SerialName("type") val type: String,   // "text", "json_object", "json_schema"
    @SerialName("json_schema") val jsonSchema: JsonElement? = null,
)

@Serializable
data class ChatCompletionResponse(
    @SerialName("id") val id: String? = null,
    @SerialName("choices") val choices: List<ChatChoice> = emptyList(),
    @SerialName("usage") val usage: UsageInfo? = null,
)

@Serializable
data class ChatChoice(
    @SerialName("index") val index: Int = 0,
    @SerialName("message") val message: ChatMessage? = null,
    @SerialName("finish_reason") val finishReason: String? = null,
)

@Serializable
data class UsageInfo(
    @SerialName("prompt_tokens") val promptTokens: Int = 0,
    @SerialName("completion_tokens") val completionTokens: Int = 0,
    @SerialName("total_tokens") val totalTokens: Int = 0,
)

// ── Image Generation ────────────────────────────────────────────────────────

@Serializable
data class ImageGenerationRequest(
    @SerialName("model") val model: String = "gpt-image-1",
    @SerialName("prompt") val prompt: String,
    @SerialName("size") val size: String = "1024x1536",
    @SerialName("quality") val quality: String = "high",
    @SerialName("n") val n: Int = 1,
)

@Serializable
data class ImageGenerationResponse(
    @SerialName("data") val data: List<ImageData> = emptyList(),
)

@Serializable
data class ImageData(
    @SerialName("b64_json") val b64Json: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("revised_prompt") val revisedPrompt: String? = null,
)
