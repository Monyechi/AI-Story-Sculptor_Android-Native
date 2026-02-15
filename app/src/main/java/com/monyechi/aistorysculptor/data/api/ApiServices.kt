package com.monyechi.aistorysculptor.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("api/v1/mobile/auth/login/")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @POST("api/v1/mobile/auth/register/")
    suspend fun register(@Body request: RegisterRequestDto): AuthResponseDto

    @POST("api/v1/mobile/auth/refresh/")
    suspend fun refreshToken(@Body request: RefreshTokenRequestDto): AuthResponseDto

    // TODO: Implement token-based auth endpoints in backend (DRF suggested).
}

interface BookApi {
    @GET("api/v1/mobile/books/")
    suspend fun getBooks(): BookListResponseDto

    @POST("api/v1/mobile/books/")
    suspend fun createBook(@Body request: CreateBookRequestDto): CreateBookResponseDto

    @GET("api/v1/mobile/jobs/{jobId}/")
    suspend fun getGenerationStatus(@Path("jobId") jobId: String): GenerationStatusDto

    @GET("api/v1/mobile/books/{bookId}/")
    suspend fun getBookDetails(@Path("bookId") bookId: String): BookDetailsDto

    @GET("api/v1/mobile/books/{bookId}/download/")
    suspend fun getBookDownload(
        @Path("bookId") bookId: String,
        @Query("format") format: String = "pdf"
    ): DownloadLinkDto

    // TODO: Confirm final mobile route names and auth policy in backend implementation.
}
