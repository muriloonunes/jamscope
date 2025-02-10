package com.murile.nowplaying

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager
import com.murile.nowplaying.worker.MyDelegatingWorkerFactory
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent

@HiltAndroidApp
class MyApp : Application() {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WorkerFactoryEntryPoint {
        fun workerFactory(): MyDelegatingWorkerFactory
    }

    override fun onCreate() {
        val workManagerConfiguration: Configuration = Configuration.Builder()
            .setWorkerFactory(EntryPoints.get(this, WorkerFactoryEntryPoint::class.java).workerFactory())
            .setMinimumLoggingLevel(Log.VERBOSE)
            .build()
        WorkManager.initialize(this, workManagerConfiguration)
        super.onCreate()
    }
}
