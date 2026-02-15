package com.monyechi.aistorysculptor.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM books WHERE userId = :userId ORDER BY createdAtIso DESC")
    fun observeBooksByUser(userId: Long): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :bookId LIMIT 1")
    suspend fun getBookById(bookId: Long): BookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: BookEntity): Long

    @Update
    suspend fun update(book: BookEntity)

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBook(bookId: Long)

    @Query("UPDATE books SET runningSummary = :summary WHERE id = :bookId")
    suspend fun updateRunningSummary(bookId: Long, summary: String)

    @Query("UPDATE books SET isRenderingChapters = :rendering WHERE id = :bookId")
    suspend fun setRenderingFlag(bookId: Long, rendering: Boolean)

    @Query("UPDATE books SET coverArtPath = :path WHERE id = :bookId")
    suspend fun setCoverArtPath(bookId: Long, path: String?)

    @Query("UPDATE books SET summary = :summary WHERE id = :bookId")
    suspend fun updateSummary(bookId: Long, summary: String)

    @Query("SELECT COUNT(*) FROM books WHERE userId = :userId")
    suspend fun countBooksByUser(userId: Long): Int
}

