package com.monyechi.aistorysculptor.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Mobile-first Retrofit contract for Django JSON endpoints.
 *
 * Base path: /api/v1/mobile/
 */
interface ApiServices {

    @POST("api/v1/mobile/auth/register/")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/v1/mobile/auth/login/")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/v1/mobile/auth/refresh/")
    suspend fun refresh(@Body request: RefreshRequest): RefreshResponse

    @GET("api/v1/mobile/books/")
    suspend fun getBooks(): LibraryResponse

    @POST("api/v1/mobile/books/")
    suspend fun createBook(@Body request: CreateBookRequest): CreateBookResponse

    @GET("api/v1/mobile/jobs/{jobId}/")
    suspend fun getJobStatus(@Path("jobId") jobId: String): JobStatusResponse

    @GET("api/v1/mobile/books/{bookId}/")
    suspend fun getBookDetails(@Path("bookId") bookId: String): BookDetailsResponse

    @GET("api/v1/mobile/books/{bookId}/download/")
    suspend fun getDownloadLink(
        @Path("bookId") bookId: String,
        @Query("format") format: String,
    ): DownloadLinkResponse
}
