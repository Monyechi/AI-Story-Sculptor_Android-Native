package com.monyechi.aistorysculptor.domain.model

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String
)

data class UserProfile(
    val userId: String,
    val email: String,
    val displayName: String?
)
