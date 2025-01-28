package com.murile.nowplaying.di

import android.app.Application
import com.murile.nowplaying.data.session.DataStoreManager
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.murile.nowplaying.data.api.ApiRequest
import com.murile.nowplaying.data.api.Exceptions
import com.murile.nowplaying.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data_store")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStorePreferences(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.userDataStore
    }

    @Provides
    @Singleton
    fun provideDataStoreManager(dataStore: DataStore<Preferences>): DataStoreManager {
        return DataStoreManager(dataStore)
    }

    @Provides
    @Singleton
    fun provideExceptions(@ApplicationContext context: Context): Exceptions {
        return Exceptions(context)
    }

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideApiRequest(exceptions: Exceptions): ApiRequest {
        return ApiRequest(exceptions)
    }

    @Provides
    @Singleton
    fun provideUserRepository(dataStoreManager: DataStoreManager, apiRequest: ApiRequest): UserRepository {
        return UserRepository(apiRequest, dataStoreManager)
    }
}