package com.monyechi.aistorysculptor.data.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Legacy route map kept for backend parity checks and migration tracking.
 *
 * These endpoints are generally session/HTML oriented in classic Django apps.
 */
interface LegacyDjangoApi {

    @FormUrlEncoded
    @POST("login/")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("register/")
    suspend fun register(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Response<ResponseBody>

    @GET("bookshelf/")
    suspend fun bookshelf(): Response<ResponseBody>

    @FormUrlEncoded
    @POST("create/")
    suspend fun create(
        @Field("title") title: String,
        @Field("summary") summary: String,
    ): Response<ResponseBody>

    @GET("book/{bookId}/details/")
    suspend fun details(@Path("bookId") bookId: String): Response<ResponseBody>

    @GET("download/pdf/{bookId}/")
    suspend fun downloadPdf(@Path("bookId") bookId: String): Response<ResponseBody>

    @GET("download/docx/{bookId}/")
    suspend fun downloadDocx(@Path("bookId") bookId: String): Response<ResponseBody>
}
