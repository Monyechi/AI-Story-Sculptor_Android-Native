package com.monyechi.aistorysculptor.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Chapter entity — mirrors Book_Assistant's Chapter model.
 * Linked to a [BookEntity] via bookId.
 */
@Entity(
    tableName = "chapters",
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
data class ChapterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: Long,

    val chapterNum: Int,
    val title: String,
    val setting: String? = null,
    val summary: String = "",
    val tone: String? = null,                  // Light | Serious | Dramatic | ...
    val desiredWordCount: Int = 1000,

    // Rendering
    val rendered: Boolean = false,
    val renderedContent: String? = null,
    val renderedSummary: String? = null,
    val partialRenderedText: String? = null,
    val currentSegment: Int = 0,
)
