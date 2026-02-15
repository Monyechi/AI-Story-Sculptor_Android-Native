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
    @SerialName("username") val username: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("agree_terms") val agreeTerms: Boolean = true
)

@Serializable
data class RefreshTokenRequestDto(
    @SerialName("refresh_token") val refreshToken: String
)

@Serializable
data class AuthResponseDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("user") val user: UserProfileDto? = null
)

@Serializable
data class UserProfileDto(
    @SerialName("id") val id: String,
    @SerialName("email") val email: String,
    @SerialName("display_name") val displayName: String? = null
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
    @SerialName("items") val books: List<BookDto>
)

@Serializable
data class CreateBookRequestDto(
    @SerialName("title") val title: String,
    @SerialName("author") val author: String,
    @SerialName("book_type") val bookType: String,
    @SerialName("genre") val genre: String,
    @SerialName("language") val language: String,
    @SerialName("pov") val pov: String,
    @SerialName("writing_style") val writingStyle: String,
    @SerialName("summary") val summary: String,
    @SerialName("character_name") val characterName: String,
    @SerialName("character_description") val characterDescription: String
    // TODO: Finalize creation payload with backend serializer.
)

@Serializable
data class CreateBookResponseDto(
    @SerialName("job_id") val jobId: String,
    @SerialName("state") val state: String,
    @SerialName("book_id") val bookId: String? = null,
    @SerialName("message") val message: String? = null
)

@Serializable
data class GenerationStatusDto(
    @SerialName("job_id") val jobId: String,
    @SerialName("state") val state: String,
    @SerialName("book_id") val bookId: String? = null,
    @SerialName("progress_percent") val progress: Int? = null,
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
    @SerialName("share_url") val shareUrl: String? = null,
    @SerialName("author") val author: String? = null,
    @SerialName("genre") val genre: String? = null,
    @SerialName("language") val language: String? = null
)

@Serializable
data class DownloadLinkDto(
    @SerialName("url") val url: String,
    @SerialName("format") val format: String,
    @SerialName("expires_at") val expiresAtIso: String? = null
)
