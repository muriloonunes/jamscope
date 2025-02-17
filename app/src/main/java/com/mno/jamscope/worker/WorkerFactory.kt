package com.mno.jamscope.worker

import android.content.Context
import androidx.work.DelegatingWorkerFactory
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.mno.jamscope.data.repository.FriendsRepository
import javax.inject.Inject
import javax.inject.Singleton

class GenericWorkerFactory @Inject constructor(
    private val repository: FriendsRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            FriendListeningWidgetWorker::class.java.name ->
                FriendListeningWidgetWorker(appContext, workerParameters, repository)
            FriendGroupWidgetWorker::class.java.name ->
                FriendGroupWidgetWorker(appContext, workerParameters, repository)
            else -> null
        }
    }
}

@Singleton
class MyDelegatingWorkerFactory @Inject constructor(
    genericWorkerFactory: GenericWorkerFactory
) : DelegatingWorkerFactory() {
    init {
        addFactory(genericWorkerFactory)
    }
}