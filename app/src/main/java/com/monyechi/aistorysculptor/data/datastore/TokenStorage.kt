package com.monyechi.aistorysculptor.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

/**
 * Persists the current logged-in user's ID.
 * No JWT tokens needed — auth is local.
 */
@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val userIdKey = longPreferencesKey("current_user_id")

    val userId: Flow<Long?> = context.dataStore.data
        .catch {
            if (it is IOException) emit(emptyPreferences()) else throw it
        }
        .map { it[userIdKey] }

    suspend fun saveUserId(id: Long) {
        context.dataStore.edit { it[userIdKey] = id }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
