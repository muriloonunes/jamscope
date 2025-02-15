package com.murile.nowplaying

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import com.murile.nowplaying.worker.MyDelegatingWorkerFactory
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent

@HiltAndroidApp
class MyApp : Application(), SingletonImageLoader.Factory {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WorkerFactoryEntryPoint {
        fun workerFactory(): MyDelegatingWorkerFactory
    }

    override fun onCreate() {
        val workManagerConfiguration: Configuration = Configuration.Builder()
            .setWorkerFactory(
                EntryPoints.get(this, WorkerFactoryEntryPoint::class.java).workerFactory()
            )
            .setMinimumLoggingLevel(Log.VERBOSE)
            .build()
        WorkManager.initialize(this, workManagerConfiguration)
        super.onCreate()
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context = context)
            .memoryCache {
                MemoryCache.Builder().maxSizePercent(percent = 0.25, context = context).build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
//            .logger(logger = DebugLogger(Logger.Level.Error))
            .build()
    }
}

