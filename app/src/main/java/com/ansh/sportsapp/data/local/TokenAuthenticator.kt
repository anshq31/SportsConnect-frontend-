package com.ansh.sportsapp.data.local

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
    override fun authenticate(route : Route?, response : okhttp3.Response ): Request? {
        if (response.code != 401) return null

        val refreshToken = runBlocking {
            authPreferences.accessToken.first()
        } ?: return null

        val refreshResult = runBlocking {
            try {
                api.get().refreshToken(RefreshRequestDto(refreshToken))
            }catch (e : Exception){
                null
            }
        } ?: return null

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