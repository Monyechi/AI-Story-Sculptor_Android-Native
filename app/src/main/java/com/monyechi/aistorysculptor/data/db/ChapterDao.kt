package com.monyechi.aistorysculptor.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {

    @Query("SELECT * FROM chapters WHERE bookId = :bookId ORDER BY chapterNum ASC")
    fun observeChaptersByBook(bookId: Long): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE bookId = :bookId ORDER BY chapterNum ASC")
    suspend fun getChaptersByBook(bookId: Long): List<ChapterEntity>

    @Query("SELECT * FROM chapters WHERE id = :chapterId LIMIT 1")
    suspend fun getChapterById(chapterId: Long): ChapterEntity?

    @Query("SELECT * FROM chapters WHERE bookId = :bookId AND rendered = 0 ORDER BY chapterNum ASC")
    suspend fun getUnrenderedChapters(bookId: Long): List<ChapterEntity>

    @Query("SELECT MAX(chapterNum) FROM chapters WHERE bookId = :bookId")
    suspend fun getMaxChapterNum(bookId: Long): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chapter: ChapterEntity): Long

    @Update
    suspend fun update(chapter: ChapterEntity)

    @Query("DELETE FROM chapters WHERE id = :chapterId")
    suspend fun deleteChapter(chapterId: Long)

    @Query("DELETE FROM chapters WHERE bookId = :bookId")
    suspend fun deleteAllByBook(bookId: Long)

    @Query("SELECT COUNT(*) FROM chapters WHERE bookId = :bookId")
    suspend fun countChaptersByBook(bookId: Long): Int

    @Query("SELECT COUNT(*) FROM chapters WHERE bookId = :bookId AND rendered = 1")
    suspend fun countRenderedChaptersByBook(bookId: Long): Int

    @Query(
        """SELECT * FROM chapters 
           WHERE bookId = :bookId AND chapterNum < :chapterNum 
           ORDER BY chapterNum DESC LIMIT 1"""
    )
    suspend fun getPreviousChapter(bookId: Long, chapterNum: Int): ChapterEntity?
}
