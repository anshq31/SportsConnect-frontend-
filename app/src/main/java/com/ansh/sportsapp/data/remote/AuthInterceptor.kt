package com.ansh.sportsapp.data.remote

import com.ansh.sportsapp.data.local.AuthPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authPreferences: AuthPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        if (path.contains("/api/auth/")) {
            return chain.proceed(request)
        }

        val token = runBlocking {
            authPreferences.accessToken.first()
        }

        return if (!token.isNullOrBlank()){
            val newRequest = request.newBuilder()
                .addHeader("Authorization","Bearer $token")
                .build()
            chain.proceed(newRequest)
        }else{
            chain.proceed(request)
        }
    }
}