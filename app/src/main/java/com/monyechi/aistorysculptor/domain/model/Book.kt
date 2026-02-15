package com.monyechi.aistorysculptor.domain.model

data class Book(
    val id: String,
    val title: String,
    val coverThumbnailUrl: String?,
    val createdAtIso: String,
    val status: String
)
