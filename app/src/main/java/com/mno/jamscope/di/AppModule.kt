package com.mno.jamscope.di

import android.app.Application
import android.content.Context
import androidx.work.WorkerFactory
import com.mno.jamscope.domain.usecase.friend.GetFriendRecentTracksUseCase
import com.mno.jamscope.ui.navigator.DefaultNavigator
import com.mno.jamscope.ui.navigator.Navigator
import com.mno.jamscope.worker.GenericWorkerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
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
}