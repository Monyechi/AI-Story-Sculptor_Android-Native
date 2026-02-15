package com.monyechi.aistorysculptor.data.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Streaming

interface LegacyDjangoApi {
    @FormUrlEncoded
    @POST("login/")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("register/")
    suspend fun register(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password1") password: String,
        @Field("password2") passwordConfirm: String,
        @Field("agree_terms") agreeTerms: Boolean
    ): Response<ResponseBody>

    @GET("bookshelf/")
    suspend fun getBookshelfHtml(): Response<ResponseBody>

    @FormUrlEncoded
    @POST("create/")
    suspend fun createBook(
        @Field("book_type") bookType: String,
        @Field("title") title: String,
        @Field("author") author: String,
        @Field("genre") genre: String,
        @Field("language") language: String,
        @Field("pov") pov: String,
        @Field("writing_style") writingStyle: String,
        @Field("summary") summary: String
    ): Response<ResponseBody>

    @GET("book/{bookId}/details/")
    suspend fun getBookDetailHtml(@Path("bookId") bookId: String): Response<ResponseBody>

    @Streaming
    @GET("download/pdf/{bookId}/")
    suspend fun downloadBookPdf(@Path("bookId") bookId: String): Response<ResponseBody>

    @Streaming
    @GET("download/docx/{bookId}/")
    suspend fun downloadBookDocx(@Path("bookId") bookId: String): Response<ResponseBody>

    // TODO: This backend currently serves HTML/session-auth flows; add JSON API endpoints for native clients.
}
