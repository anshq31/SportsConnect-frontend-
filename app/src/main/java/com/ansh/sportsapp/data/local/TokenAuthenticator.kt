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
    private val api: Lazy<SportsApi>
) : Authenticator {

    // Use a ReentrantLock so we can check if already locked (avoid deadlock)
    private val lock = java.util.concurrent.locks.ReentrantLock()

    override fun authenticate(route: Route?, response: Response): Request? {

        // 1. Never attempt to refresh the refresh endpoint itself
        if (response.request.url.encodedPath.contains("/api/auth/refresh")) {
            Log.d("TOKEN_AUTH", "Refresh endpoint returned 401 — clearing auth data")
            runBlocking { authPreferences.clearAuthData() }
            return null
        }

        // 2. Give up after too many retries on the same request
        if (responseCount(response) >= 3) {
            Log.d("TOKEN_AUTH", "Giving up after 3 retries")
            return null
        }

        // 3. Only one thread should refresh at a time
        //    If the lock is already held by THIS thread, we are in a recursive call — bail out
        if (lock.isHeldByCurrentThread) {
            Log.d("TOKEN_AUTH", "Recursive authenticate call detected — bailing out")
            return null
        }

        lock.lock()
        try {
            // 4. Re-read the latest token AFTER acquiring the lock.
            //    Another thread may have already refreshed it while we were waiting.
            val requestToken = response.request.header("Authorization")
                ?.removePrefix("Bearer ")
                ?.trim()

            val latestAccessToken = runBlocking { authPreferences.accessToken.first() }

            Log.d(
                "TOKEN_AUTH",
                "requestToken=${requestToken?.takeLast(10)}, latestToken=${latestAccessToken?.takeLast(10)}"
            )

            // If the stored token is already different (another thread refreshed it), just retry
            if (!latestAccessToken.isNullOrBlank() && latestAccessToken != requestToken) {
                Log.d("TOKEN_AUTH", "Token already refreshed by another thread, retrying")
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $latestAccessToken")
                    .build()
            }

            // 5. Perform the actual refresh
            val refreshToken = runBlocking { authPreferences.refreshToken.first() }

            if (refreshToken.isNullOrBlank()) {
                Log.e("TOKEN_AUTH", "No refresh token stored — cannot refresh")
                return null
            }

            return try {
                Log.d("TOKEN_AUTH", "Calling refresh endpoint...")
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
                }

                Log.d("TOKEN_AUTH", "Token refreshed successfully")

                response.request.newBuilder()
                    .header("Authorization", "Bearer ${refreshResponse.accessToken}")
                    .build()

            } catch (e: Exception) {
                Log.e("TOKEN_AUTH", "Refresh failed: ${e.message}", e)
                runBlocking { authPreferences.clearAuthData() }
                null
            }

        } finally {
            lock.unlock()
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}