package com.mno.jamscope.data.repository

import android.content.Context
import com.mno.jamscope.data.local.datastore.SettingsDataStore
import com.mno.jamscope.data.local.datastore.UserDataStore
import com.mno.jamscope.data.local.db.dao.FriendsDao
import com.mno.jamscope.data.local.db.dao.TrackDao
import com.mno.jamscope.data.local.db.dao.UserDao
import com.mno.jamscope.data.remote.api.LastFmServiceApi
import com.mno.jamscope.domain.repository.FriendRepository
import com.mno.jamscope.domain.repository.LoginRepository
import com.mno.jamscope.domain.repository.SettingsRepository
import com.mno.jamscope.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideLoginRepository(
        serviceApi: LastFmServiceApi,
        userDataStore: UserDataStore,
        @ApplicationContext context: Context,
    ): LoginRepository {
        return LoginRepositoryImpl(
            serviceApi = serviceApi,
            userDataStore = userDataStore,
            context = context
        )
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        serviceApi: LastFmServiceApi,
        userDataStore: UserDataStore,
        settingsDataStore: SettingsDataStore,
        userDao: UserDao,
        trackDao: TrackDao,
        @ApplicationContext context: Context,
    ): UserRepository {
        return UserRepositoryImpl(
            serviceApi = serviceApi,
            userDataStore = userDataStore,
            settingsDataStore = settingsDataStore,
            userDao = userDao,
            trackDao = trackDao,
            context = context
        )
    }

    @Provides
    @Singleton
    fun provideFriendRepository(
        serviceApi: LastFmServiceApi,
        settingsDataStore: SettingsDataStore,
        friendsDao: FriendsDao,
        trackDao: TrackDao,
        @ApplicationContext context: Context,
    ): FriendRepository {
        return FriendRepositoryImpl(
            serviceApi = serviceApi,
            settingsDataStore = settingsDataStore,
            friendsDao = friendsDao,
            trackDao = trackDao,
            context = context,
        )
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        settingsDataStore: SettingsDataStore,
    ): SettingsRepository {
        return SettingsRepositoryImpl(
            settingsDataStore = settingsDataStore
        )
    }
}