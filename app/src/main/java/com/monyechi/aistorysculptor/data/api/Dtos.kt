package com.monyechi.aistorysculptor.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)

@Serializable
data class RegisterRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("display_name") val displayName: String
)

@Serializable
data class RefreshTokenRequestDto(
    @SerialName("refresh_token") val refreshToken: String
)

@Serializable
data class AuthResponseDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String
    // TODO: Add user payload mapping from backend response if returned.
)

@Serializable
data class BookDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("cover_thumbnail_url") val coverThumbnailUrl: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("status") val status: String
    // TODO: Align these JSON field names with your Django API serializer fields.
)

@Serializable
data class BookListResponseDto(
    @SerialName("results") val books: List<BookDto>
    // TODO: If API returns {"books": [...]}, rename this mapping.
)

@Serializable
data class CreateBookRequestDto(
    @SerialName("genre") val genre: String,
    @SerialName("age_group") val ageGroup: String,
    @SerialName("character_name") val characterName: String,
    @SerialName("character_description") val characterDescription: String,
    @SerialName("story_outline") val storyOutline: String
    // TODO: Replace fields if your backend expects different creation payload keys.
)

@Serializable
data class CreateBookResponseDto(
    @SerialName("job_id") val jobId: String,
    @SerialName("status") val status: String,
    @SerialName("book_id") val bookId: String? = null,
    @SerialName("message") val message: String? = null
)

@Serializable
data class GenerationStatusDto(
    @SerialName("job_id") val jobId: String,
    @SerialName("status") val status: String,
    @SerialName("book_id") val bookId: String? = null,
    @SerialName("progress") val progress: Int? = null,
    @SerialName("message") val message: String? = null
)

@Serializable
data class BookChapterDto(
    @SerialName("index") val index: Int,
    @SerialName("title") val title: String,
    @SerialName("content") val content: String
)

@Serializable
data class BookDetailsDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("cover_image_url") val coverImageUrl: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("status") val status: String,
    @SerialName("chapters") val chapters: List<BookChapterDto> = emptyList(),
    @SerialName("download_url") val downloadUrl: String? = null,
    @SerialName("share_url") val shareUrl: String? = null
    // TODO: Align chapter and URL fields to your backend serializer response.
)
