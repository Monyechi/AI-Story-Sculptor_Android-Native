package com.monyechi.aistorysculptor.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Character entity — mirrors Book_Assistant's Character model.
 * Linked to a [BookEntity] via bookId.
 */
@Entity(
    tableName = "characters",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("bookId")]
)
data class CharacterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: Long,

    val name: String,
    val age: Int? = null,
    val bio: String? = null,
    val role: String,   // "Main Character" | "Opposing Character" | "Supporting"
)
