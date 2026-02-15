package com.monyechi.aistorysculptor.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): UserEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Update
    suspend fun update(user: UserEntity)

    @Query("UPDATE users SET tokens = tokens + :amount WHERE id = :userId")
    suspend fun addTokens(userId: Long, amount: Int)

    @Query("UPDATE users SET tokens = tokens - :amount WHERE id = :userId AND tokens >= :amount")
    suspend fun subtractTokens(userId: Long, amount: Int): Int  // returns rows affected

    @Query("SELECT tokens FROM users WHERE id = :userId")
    suspend fun getTokenBalance(userId: Long): Int?

    @Query("SELECT tokens FROM users WHERE id = :userId")
    fun observeTokenBalance(userId: Long): Flow<Int?>

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Long)
}
