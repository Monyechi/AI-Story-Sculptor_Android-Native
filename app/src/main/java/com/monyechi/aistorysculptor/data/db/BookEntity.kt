package com.monyechi.aistorysculptor.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val coverThumbnailUrl: String?,
    val createdAtIso: String,
    val status: String
)
