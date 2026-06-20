package com.ansh.sportsapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class AuthPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object{
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val BLOCKED_USER_IDS_KEY = stringPreferencesKey("blocked_user_ids")
        private val BLOCKED_USER_ENTRIES_KEY = stringPreferencesKey("blocked_user_entries")
    }

    suspend fun saveAuthData(accessToken : String , refreshToken : String, userId : String,username: String){
        context.dataStore.edit{prefs->
            prefs[ACCESS_TOKEN_KEY] = accessToken
            prefs[REFRESH_TOKEN_KEY] = refreshToken
            prefs[USER_ID_KEY] = userId
            prefs[USERNAME_KEY] = username
        }
    }

    val accessToken : Flow<String?> = context.dataStore.data.map { prefs->
        prefs[ACCESS_TOKEN_KEY]
    }

    val refreshToken : Flow<String?> = context.dataStore.data.map { prefs->
        prefs[REFRESH_TOKEN_KEY]
    }

    val username: Flow<String?> = context.dataStore.data.map {prefs->
        prefs[USERNAME_KEY]
    }

    val blockedUserIds: Flow<Set<Long>> = context.dataStore.data.map { prefs ->
        prefs[BLOCKED_USER_IDS_KEY]
            ?.split(",")
            ?.filter { it.isNotBlank() }
            ?.mapNotNull { it.toLongOrNull() }
            ?.toSet()
            ?: emptySet()
    }

    suspend fun saveBlockedUserIds(ids: List<Long>) {
        context.dataStore.edit { prefs ->
            prefs[BLOCKED_USER_IDS_KEY] = ids.joinToString(",")
        }
    }

    suspend fun addBlockedUserId(id: Long) {
        context.dataStore.edit { prefs ->
            val current = prefs[BLOCKED_USER_IDS_KEY]
                ?.split(",")?.filter { it.isNotBlank() }?.toMutableSet() ?: mutableSetOf()
            current.add(id.toString())
            prefs[BLOCKED_USER_IDS_KEY] = current.joinToString(",")
        }
    }

    suspend fun removeBlockedUserId(id: Long) {
        context.dataStore.edit { prefs ->
            val current = prefs[BLOCKED_USER_IDS_KEY]
                ?.split(",")?.filter { it.isNotBlank() && it != id.toString() }
                ?: emptyList()
            prefs[BLOCKED_USER_IDS_KEY] = current.joinToString(",")
        }
    }

    val blockedUserEntries: Flow<List<BlockedUserEntry>> = context.dataStore.data.map { prefs ->
        prefs[BLOCKED_USER_ENTRIES_KEY]
            ?.split(",")
            ?.filter { it.isNotBlank() }
            ?.mapNotNull { entry ->
                val parts = entry.split(":")
                if (parts.size >= 2) {
                    val id = parts[0].toLongOrNull() ?: return@mapNotNull null
                    val username = parts.drop(1).joinToString(":")
                    BlockedUserEntry(id, username)
                } else null
            }
            ?: emptyList()
    }

    suspend fun saveBlockedUserEntry(entry: BlockedUserEntry) {
        context.dataStore.edit { prefs ->
            val current = prefs[BLOCKED_USER_ENTRIES_KEY]
                ?.split(",")?.filter { it.isNotBlank() }?.toMutableList() ?: mutableListOf()
            val key = "${entry.userId}:${entry.username}"
            if (!current.contains(key)) current.add(key)
            prefs[BLOCKED_USER_ENTRIES_KEY] = current.joinToString(",")
        }
    }

    suspend fun removeBlockedUserEntry(userId: Long) {
        context.dataStore.edit { prefs ->
            val current = prefs[BLOCKED_USER_ENTRIES_KEY]
                ?.split(",")
                ?.filter { it.isNotBlank() && !it.startsWith("$userId:") }
                ?: emptyList()
            prefs[BLOCKED_USER_ENTRIES_KEY] = current.joinToString(",")
        }
    }

    suspend fun clearAuthData(){
        context.dataStore.edit { prefs->
            prefs.clear()
        }
    }
}