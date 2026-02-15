package com.monyechi.aistorysculptor.data.repository

import com.monyechi.aistorysculptor.data.datastore.TokenStorage
import com.monyechi.aistorysculptor.data.db.UserDao
import com.monyechi.aistorysculptor.data.db.UserEntity
import com.monyechi.aistorysculptor.domain.common.AppResult
import com.monyechi.aistorysculptor.domain.model.UserProfile
import com.monyechi.aistorysculptor.domain.repository.AuthRepository
import kotlinx.coroutines.flow.firstOrNull
import java.security.MessageDigest
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local-first auth — no remote server. Users stored in Room.
 * Passwords hashed with SHA-256 (upgrade to bcrypt/argon2 for production).
 * Current user ID persisted in DataStore.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val tokenStorage: TokenStorage,
) : AuthRepository {

    override suspend fun register(
        username: String,
        email: String,
        password: String,
        displayName: String
    ): AppResult<UserProfile> {
        return try {
            val existing = userDao.getByEmail(email)
            if (existing != null) {
                return AppResult.Failure("An account with this email already exists.")
            }
            val entity = UserEntity(
                username = username,
                email = email,
                displayName = displayName.ifBlank { username },
                passwordHash = hashPassword(password),
                tokens = 50,  // Welcome bonus
                agreedToTerms = true,
                createdAtIso = Instant.now().toString(),
            )
            val userId = userDao.insert(entity)
            tokenStorage.saveUserId(userId)
            val saved = userDao.getById(userId)!!
            AppResult.Success(saved.toDomain())
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Registration failed", t)
        }
    }

    override suspend fun login(email: String, password: String): AppResult<UserProfile> {
        return try {
            val user = userDao.getByEmail(email)
                ?: return AppResult.Failure("No account found with this email.")
            if (user.passwordHash != hashPassword(password)) {
                return AppResult.Failure("Incorrect password.")
            }
            tokenStorage.saveUserId(user.id)
            AppResult.Success(user.toDomain())
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Login failed", t)
        }
    }

    override suspend fun logout() {
        tokenStorage.clearAll()
    }

    override suspend fun isLoggedIn(): Boolean {
        val userId = tokenStorage.userId.firstOrNull() ?: return false
        return userId > 0
    }

    override suspend fun getCurrentUser(): UserProfile? {
        val userId = tokenStorage.userId.firstOrNull() ?: return null
        return userDao.getById(userId)?.toDomain()
    }

    override suspend fun getTokenBalance(): Int {
        val userId = tokenStorage.userId.firstOrNull() ?: return 0
        return userDao.getTokenBalance(userId) ?: 0
    }

    override suspend fun addTokens(amount: Int): AppResult<Unit> {
        return try {
            val userId = tokenStorage.userId.firstOrNull()
                ?: return AppResult.Failure("Not logged in")
            userDao.addTokens(userId, amount)
            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Failed to add tokens", t)
        }
    }

    override suspend fun subtractTokens(amount: Int): AppResult<Unit> {
        return try {
            val userId = tokenStorage.userId.firstOrNull()
                ?: return AppResult.Failure("Not logged in")
            val affected = userDao.subtractTokens(userId, amount)
            if (affected == 0) {
                AppResult.Failure("Not enough tokens.")
            } else {
                AppResult.Success(Unit)
            }
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Failed to subtract tokens", t)
        }
    }

    override suspend fun deleteAccount(): AppResult<Unit> {
        return try {
            val userId = tokenStorage.userId.firstOrNull()
                ?: return AppResult.Failure("Not logged in")
            userDao.deleteUser(userId)
            tokenStorage.clearAll()
            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Failure(t.message ?: "Failed to delete account", t)
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(password.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    private fun UserEntity.toDomain(): UserProfile = UserProfile(
        id = id,
        username = username,
        email = email,
        displayName = displayName,
        tokens = tokens,
        agreedToTerms = agreedToTerms,
    )
}
