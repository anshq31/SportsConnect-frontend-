package com.ansh.sportsapp.data.di

import com.ansh.sportsapp.data.local.AuthPreferences
import com.ansh.sportsapp.data.remote.websocket.StompWebSocketManager
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {
    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()



    @Provides
    @Singleton
    fun provideWebSocketManager(
        client: OkHttpClient,
        authPreferences: AuthPreferences,
        gson: Gson
    ): StompWebSocketManager {
        return StompWebSocketManager(client, authPreferences, gson)
    }
}