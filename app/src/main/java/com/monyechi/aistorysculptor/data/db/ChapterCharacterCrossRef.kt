package com.monyechi.aistorysculptor.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Junction table linking chapters to characters (many-to-many).
 */
@Entity(
    tableName = "chapter_character_cross_ref",
    primaryKeys = ["chapterId", "characterId"],
    foreignKeys = [
        ForeignKey(
            entity = ChapterEntity::class,
            parentColumns = ["id"],
            childColumns = ["chapterId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = CharacterEntity::class,
            parentColumns = ["id"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("characterId")]
)
data class ChapterCharacterCrossRef(
    val chapterId: Long,
    val characterId: Long,
)
