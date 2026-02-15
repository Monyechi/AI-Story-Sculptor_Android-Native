package com.monyechi.aistorysculptor.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login/")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @POST("api/auth/register/")
    suspend fun register(@Body request: RegisterRequestDto): AuthResponseDto

    @POST("api/auth/refresh/")
    suspend fun refreshToken(@Body request: RefreshTokenRequestDto): AuthResponseDto

    // TODO: Replace endpoint paths above with exact Django endpoints.
}

interface BookApi {
    @GET("api/books/")
    suspend fun getBooks(): BookListResponseDto

    @POST("api/books/create/")
    suspend fun createBook(@Body request: CreateBookRequestDto): CreateBookResponseDto

    @GET("api/books/generation/{jobId}/")
    suspend fun getGenerationStatus(@Path("jobId") jobId: String): GenerationStatusDto

    @GET("api/books/{bookId}/")
    suspend fun getBookDetails(@Path("bookId") bookId: String): BookDetailsDto

    // TODO: Verify exact path naming and trailing slash conventions with Django URLs.
}
