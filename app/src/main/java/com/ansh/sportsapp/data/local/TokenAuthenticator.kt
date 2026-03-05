package com.ansh.sportsapp.data.local

import android.util.Log
import com.ansh.sportsapp.data.remote.SportsApi
import com.ansh.sportsapp.data.remote.dto.auth.RefreshRequestDto
import dagger.Lazy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val authPreferences: AuthPreferences,
    private val api : Lazy<SportsApi>
) : Authenticator {

    private val lock = Any()

    override fun authenticate(route : Route?, response : okhttp3.Response ): Request? {

        synchronized(lock){

            Log.d("TOKEN_AUTH", "401 received — attempting token refresh")

            if (response.request.header("X-Retry") != null) {
                Log.d("TOKEN_AUTH", "Already retried — giving up, clearing auth")
                runBlocking { authPreferences.clearAuthData() }
                return null
            }

            if (response.request.url.encodedPath.contains("/api/auth/")) {
                Log.d("TOKEN_AUTH", "Auth endpoint failed — not retrying")
                runBlocking { authPreferences.clearAuthData() }
                return null
            }

//        if (response.code != 401) return null

            val currentToken = runBlocking {
                authPreferences.accessToken.first()
            }

            val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")


            if (currentToken != null && currentToken != requestToken){
                Log.d("TOKEN_AUTH", "Token already refreshed by another thread")
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .header("X-Retry", "true")
                    .build()
            }

            val refreshToken = runBlocking {
                authPreferences.refreshToken.first()
            }

            if (refreshToken.isNullOrBlank()) {
                Log.d("TOKEN_AUTH", "No refresh token found — clearing auth")
                runBlocking { authPreferences.clearAuthData() }
                return null
            }

            val refreshResult = runBlocking {
                try {
                    api.get().refreshToken(RefreshRequestDto(refreshToken))
                }catch (e : Exception){
                    Log.e("TOKEN_AUTH", "Refresh call failed: ${e.message}")
                    null
                }
            }
            if (refreshResult == null) {
                Log.d("TOKEN_AUTH", "Refresh returned null — clearing auth")
                runBlocking { authPreferences.clearAuthData() }
                return null
            }

            Log.d("TOKEN_AUTH", "Token refreshed successfully!")

            runBlocking {
                authPreferences.saveAuthData(
                    accessToken = refreshResult.accessToken,
                    refreshToken = refreshResult.refreshToken,
                    userId = refreshResult.id.toString(),
                    username = refreshResult.username
                )
            }
            return response.request.newBuilder()
                .header("Authorization","Bearer ${refreshResult.accessToken}")
                .header("X-Retry", "true")
                .build()
        }

    }
}