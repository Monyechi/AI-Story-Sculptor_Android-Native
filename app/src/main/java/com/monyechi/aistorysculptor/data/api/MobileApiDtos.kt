package com.monyechi.aistorysculptor.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    @SerialName("username") val username: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("agree_terms") val agreeTerms: Boolean,
)

@Serializable
data class LoginRequest(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
)

@Serializable
data class RefreshRequest(
    @SerialName("refresh_token") val refreshToken: String,
)

@Serializable
data class AuthResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("user") val user: MobileUser,
)

@Serializable
data class RefreshResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
)

@Serializable
data class MobileUser(
    @SerialName("id") val id: String,
    @SerialName("email") val email: String,
    @SerialName("display_name") val displayName: String,
)

@Serializable
data class LibraryResponse(
    @SerialName("items") val items: List<BookListItem> = emptyList(),
)

@Serializable
data class BookListItem(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("cover_thumbnail_url") val coverThumbnailUrl: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("status") val status: String,
)

@Serializable
data class CreateBookRequest(
    @SerialName("title") val title: String,
    @SerialName("author") val author: String,
    @SerialName("book_type") val bookType: String,
    @SerialName("genre") val genre: String,
    @SerialName("language") val language: String,
    @SerialName("pov") val pov: String,
    @SerialName("writing_style") val writingStyle: String,
    @SerialName("summary") val summary: String,
    @SerialName("character_name") val characterName: String,
    @SerialName("character_description") val characterDescription: String,
)

@Serializable
data class CreateBookResponse(
    @SerialName("job_id") val jobId: String,
    @SerialName("state") val state: String,
    @SerialName("book_id") val bookId: String? = null,
    @SerialName("message") val message: String? = null,
)

@Serializable
data class JobStatusResponse(
    @SerialName("job_id") val jobId: String,
    @SerialName("state") val state: String,
    @SerialName("book_id") val bookId: String? = null,
    @SerialName("progress_percent") val progressPercent: Int? = null,
    @SerialName("message") val message: String? = null,
)

@Serializable
data class BookDetailsResponse(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("author") val author: String,
    @SerialName("genre") val genre: String,
    @SerialName("language") val language: String,
    @SerialName("cover_image_url") val coverImageUrl: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("status") val status: String,
    @SerialName("download_url") val downloadUrl: String? = null,
    @SerialName("share_url") val shareUrl: String? = null,
    @SerialName("chapters") val chapters: List<ChapterDto> = emptyList(),
)

@Serializable
data class ChapterDto(
    @SerialName("index") val index: Int,
    @SerialName("title") val title: String,
    @SerialName("content") val content: String,
)

@Serializable
data class DownloadLinkResponse(
    @SerialName("url") val url: String,
    @SerialName("format") val format: String,
    @SerialName("expires_at") val expiresAt: String,
)
