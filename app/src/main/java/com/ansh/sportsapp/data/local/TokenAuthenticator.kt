package com.ansh.sportsapp.data.local

import android.util.Log
import com.ansh.sportsapp.data.remote.SportsApi
import com.ansh.sportsapp.data.remote.dto.auth.RefreshRequestDto
import dagger.Lazy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val authPreferences: AuthPreferences,
    private val api : Lazy<SportsApi>
) : Authenticator {

    private val lock = Any()

    override fun authenticate(route : Route?, response : Response ): Request? {

        synchronized(lock){
            if (response.code != 401) return null

            val refreshToken = runBlocking {
                authPreferences.refreshToken.first()
            } ?: return null

            return try {
                val refreshResponse = runBlocking {
                    api.get().refreshToken(RefreshRequestDto(refreshToken))
                }

                runBlocking {
                    authPreferences.saveAuthData(
                        accessToken = refreshResponse.accessToken,
                        refreshToken = refreshResponse.refreshToken,
                        userId = refreshResponse.id.toString(),
                        username = refreshResponse.username
                    )

                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${refreshResponse.accessToken}")
                        .build()
                }
            }catch (e : Exception) {
                runBlocking { authPreferences.clearAuthData() }
                null
            }
        }
    }
}