package com.mno.jamscope.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.work.WorkerFactory
import com.mno.jamscope.data.api.ApiRequest
import com.mno.jamscope.data.api.Exceptions
import com.mno.jamscope.data.local.AppDatabase
import com.mno.jamscope.data.local.dao.FriendsDao
import com.mno.jamscope.data.local.dao.UserProfileDao
import com.mno.jamscope.data.repository.FriendsRepository
import com.mno.jamscope.data.repository.UserRepository
import com.mno.jamscope.data.session.UserDataStoreManager
import com.mno.jamscope.worker.GenericWorkerFactory
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
    fun provideUserDataStorePreferences(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.userDataStore
    }

    @Provides
    @Singleton
    fun provideUserDataStoreManager(dataStore: DataStore<Preferences>): UserDataStoreManager {
        return UserDataStoreManager(dataStore)
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
    fun provideUserRepository(dataStoreManager: UserDataStoreManager, apiRequest: ApiRequest, userProfileDao: UserProfileDao): UserRepository {
        return UserRepository(apiRequest, dataStoreManager, userProfileDao)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideFriendsDao(database: AppDatabase): FriendsDao = database.friendsDao()

    @Provides
    @Singleton
    fun provideFriendsRepository(dataStoreManager: UserDataStoreManager, friendsDao: FriendsDao, apiRequest: ApiRequest): FriendsRepository {
        return FriendsRepository(dataStoreManager, friendsDao, apiRequest)
    }

    @Provides
    fun provideWorkerFactory(
        repository: FriendsRepository
    ): WorkerFactory {
        return GenericWorkerFactory(repository)
    }

    @Provides
    @Singleton
    fun provideUserProfileDao(database: AppDatabase): UserProfileDao = database.userProfileDao()
}