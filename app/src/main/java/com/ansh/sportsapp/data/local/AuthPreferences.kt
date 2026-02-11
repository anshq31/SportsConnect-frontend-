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

    val username: Flow<String?> = context.dataStore.data.map {prefs->
        prefs[USERNAME_KEY]
    }

    suspend fun clearAuthData(){
        context.dataStore.edit { prefs->
            prefs.clear()
        }
    }
}