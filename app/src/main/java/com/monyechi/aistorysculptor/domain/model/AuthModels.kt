package com.monyechi.aistorysculptor.domain.model

/** Locally-stored user profile. */
data class UserProfile(
    val id: Long,
    val username: String,
    val email: String,
    val displayName: String = "",
    val tokens: Int = 0,
    val agreedToTerms: Boolean = false,
)

