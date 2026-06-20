package com.ansh.sportsapp.data.di

import com.ansh.sportsapp.data.remote.NominatimApi
import com.ansh.sportsapp.data.repository.LocationRepositoryImpl
import com.ansh.sportsapp.domain.repository.LocationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NominatimRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NominatimModule {

    @Provides
    @Singleton
    @NominatimRetrofit
    fun provideNominatimOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .header("User-Agent", "SportsConnect/1.0")
                    .build()
                chain.proceed(req)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideNominatimApi(@NominatimRetrofit client: OkHttpClient): NominatimApi {
        return Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NominatimApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(impl: LocationRepositoryImpl): LocationRepository = impl
}
