package com.mno.jamscope.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.WorkerFactory
import com.mno.jamscope.data.local.datastore.SettingsDataStoreManager
import com.mno.jamscope.data.local.datastore.UserDataStoreManager
import com.mno.jamscope.data.local.db.dao.FriendsDao
import com.mno.jamscope.data.local.db.dao.UserDao
import com.mno.jamscope.data.remote.api.AuthRequest
import com.mno.jamscope.data.remote.api.Exceptions
import com.mno.jamscope.data.remote.api.FriendRequest
import com.mno.jamscope.data.remote.api.UserRequest
import com.mno.jamscope.data.repository.ApiRepository
import com.mno.jamscope.data.repository.FriendsRepository
import com.mno.jamscope.data.repository.SettingsRepository
import com.mno.jamscope.data.repository.UserRepository
import com.mno.jamscope.domain.usecase.friend.GetFriendRecentTracksUseCase
import com.mno.jamscope.ui.navigator.DefaultNavigator
import com.mno.jamscope.ui.navigator.Navigator
import com.mno.jamscope.worker.GenericWorkerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "old_user_data_store")

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
    fun provideApiRepository(
        authRequest: AuthRequest,
        friendRequest: FriendRequest,
        userRequest: UserRequest,
    ): ApiRepository {
        return ApiRepository(
            authRequest = authRequest,
            friendRequest = friendRequest,
            userRequest = userRequest
        )
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        dataStoreManager: UserDataStoreManager,
        apiRepository: ApiRepository,
        userDao: UserDao,
    ): UserRepository {
        return UserRepository(apiRepository, dataStoreManager, userDao)
    }

    @Provides
    @Singleton
    fun provideFriendsRepository(
        dataStoreManager: UserDataStoreManager,
        friendsDao: FriendsDao,
        apiRepository: ApiRepository,
    ): FriendsRepository {
        return FriendsRepository(dataStoreManager, friendsDao, apiRepository)
    }

    @Provides
    fun provideWorkerFactory(
        getFriendRecentTracksUseCase: GetFriendRecentTracksUseCase,
    ): WorkerFactory {
        return GenericWorkerFactory(getFriendRecentTracksUseCase)
    }

    @Provides
    @Singleton
    fun provideNavigator(): Navigator = DefaultNavigator()

    @Provides
    @Singleton
    fun provideSettingsDataStoreManager(dataStore: DataStore<Preferences>): SettingsDataStoreManager =
        SettingsDataStoreManager(dataStore)

    @Provides
    @Singleton
    fun provideSettingsRepository(settingsDataStoreManager: SettingsDataStoreManager): SettingsRepository {
        return SettingsRepository(settingsDataStoreManager)
    }

    @Provides
    @Singleton
    fun provideAuthRequest(
        exceptions: Exceptions,
        client: HttpClient,
    ): AuthRequest {
        return AuthRequest(exceptions, client)
    }

    @Provides
    @Singleton
    fun provideProfileRequest(
        exceptions: Exceptions,
        client: HttpClient,
    ): UserRequest {
        return UserRequest(exceptions, client)
    }
}