    package com.ansh.sportsapp.data.di

    import android.content.Context
    import androidx.room.Room
    import com.ansh.sportsapp.data.local.database.SportsDatabase
    import com.ansh.sportsapp.data.local.database.chat.ChatMessageDao
    import com.ansh.sportsapp.data.remote.SportsApi
    import com.ansh.sportsapp.data.repository.AuthRepositoryImpl
    import com.ansh.sportsapp.data.repository.ChatRepositoryImpl
    import com.ansh.sportsapp.data.repository.GigRepositoryImpl
    import com.ansh.sportsapp.domain.repository.AuthRepository
    import com.ansh.sportsapp.domain.repository.ChatRepository
    import com.ansh.sportsapp.domain.repository.GigRepository
    import dagger.Module
    import dagger.Provides
    import dagger.hilt.InstallIn
    import dagger.hilt.android.qualifiers.ApplicationContext
    import dagger.hilt.components.SingletonComponent
    import retrofit2.Retrofit
    import javax.inject.Singleton

    @Module
    @InstallIn(SingletonComponent::class)
    object AppModule {

        @Provides
        @Singleton
        fun provideSportsApi(retrofit: Retrofit): SportsApi{
            return retrofit.create(SportsApi::class.java)
        }

        @Provides
        @Singleton
        fun provideAuthRepository(repository: AuthRepositoryImpl): AuthRepository{
            return repository
        }

        @Provides
        @Singleton
        fun provideGigRepository(repository: GigRepositoryImpl): GigRepository{
            return repository
        }

        @Provides
        @Singleton
        fun provideChatRepository(repository: ChatRepositoryImpl): ChatRepository{
            return repository
        }

        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext context: Context
        ): SportsDatabase {
            return Room.databaseBuilder<SportsDatabase>(
                context,
                SportsDatabase::class.java,
                "sports_connect"
            ).build()
        }

        @Provides
        fun provideChatMessageDao(
            database: SportsDatabase
        ): ChatMessageDao{
            return database.chatMessageDao()
        }
    }