package com.monyechi.aistorysculptor.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Full Book entity — mirrors Book_Assistant's Book model.
 * userId references the local [UserEntity].
 */
@Entity(
    tableName = "books",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("userId")]
)
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,

    // Basic info
    val title: String,
    val author: String = "",
    val bookType: String = "children",       // children | self-help | fiction-novel | non-fiction-novel
    val genre: String = "",
    val language: String = "English",
    val pov: String = "",                    // First Person | Second Person | Third Person Omniscient | Third Person Limited
    val writingStyle: String = "",           // descriptive | concise | dialogue-driven | ...
    val summary: String = "",
    val runningSummary: String = "",          // updated after each chapter render

    // Cover art
    val coverArtPath: String? = null,        // local file path to cover image
    val isGeneratingCoverArt: Boolean = false,
    val tokenChargeForImage: Int = 15,

    // Rendering state
    val isRenderingChapters: Boolean = false,

    // Timestamps
    val createdAtIso: String = "",
)

