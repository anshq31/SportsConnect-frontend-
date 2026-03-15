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
            if(response.request.url.encodedPath.contains("/api/auth/refresh")) return null
            if (responseCount(response) >= 2)return null

            val requestToken = response.request.header("Authorization")
                ?.removePrefix("Bearer ")
                ?.trim()

            val latestAccessToken = runBlocking { authPreferences.accessToken.first() }

            // If another request already refreshed the token, just retry with the latest token.
            if (!latestAccessToken.isNullOrBlank() && latestAccessToken != requestToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $latestAccessToken")
                    .build()
            }

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

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse

        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }

        return count
    }

}