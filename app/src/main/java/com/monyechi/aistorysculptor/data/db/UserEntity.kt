package com.monyechi.aistorysculptor.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local user account — mirrors Book_Assistant's CustomUser.
 * Auth is local-first; no remote server dependency.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val email: String,
    val displayName: String = "",
    val passwordHash: String,           // bcrypt or argon2 hash
    val tokens: Int = 0,                // virtual currency balance
    val agreedToTerms: Boolean = false,
    val createdAtIso: String = "",
    val lastSeenIso: String? = null,
)
