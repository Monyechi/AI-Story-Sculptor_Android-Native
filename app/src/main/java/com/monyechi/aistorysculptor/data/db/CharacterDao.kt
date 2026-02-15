package com.monyechi.aistorysculptor.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    @Query("SELECT * FROM characters WHERE bookId = :bookId ORDER BY name ASC")
    fun observeCharactersByBook(bookId: Long): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE bookId = :bookId ORDER BY name ASC")
    suspend fun getCharactersByBook(bookId: Long): List<CharacterEntity>

    @Query("SELECT * FROM characters WHERE id = :characterId LIMIT 1")
    suspend fun getCharacterById(characterId: Long): CharacterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(character: CharacterEntity): Long

    @Update
    suspend fun update(character: CharacterEntity)

    @Query("DELETE FROM characters WHERE id = :characterId")
    suspend fun deleteCharacter(characterId: Long)

    @Query("DELETE FROM characters WHERE bookId = :bookId")
    suspend fun deleteAllByBook(bookId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapterCharacterCrossRef(crossRef: ChapterCharacterCrossRef)

    @Query("DELETE FROM chapter_character_cross_ref WHERE chapterId = :chapterId")
    suspend fun clearChapterCharacters(chapterId: Long)

    @Query(
        """SELECT c.* FROM characters c
           INNER JOIN chapter_character_cross_ref x ON c.id = x.characterId
           WHERE x.chapterId = :chapterId"""
    )
    suspend fun getCharactersForChapter(chapterId: Long): List<CharacterEntity>
}
