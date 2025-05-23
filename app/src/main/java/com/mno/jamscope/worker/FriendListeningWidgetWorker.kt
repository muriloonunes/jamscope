package com.mno.jamscope.worker

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mno.jamscope.data.model.User
import com.mno.jamscope.data.repository.FriendsRepository
import com.mno.jamscope.widget.WidgetDataStoreManager
import com.mno.jamscope.widget.singlefriend.FriendListeningWidget

class FriendListeningWidgetWorker(
    private val appContext: Context,
    workerParams: WorkerParameters,
    var repository: FriendsRepository
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val glanceIds = GlanceAppWidgetManager(appContext)
            .getGlanceIds(FriendListeningWidget::class.java)

        if (glanceIds.isEmpty()) return Result.failure()
        try {
            for (glanceId in glanceIds) {
                val prefs = FriendListeningWidget().getAppWidgetState<Preferences>(appContext, glanceId)
                val friend = WidgetDataStoreManager.getFriend(prefs)

                friend?.let {
                    repository.getRecentTracks(it)
                    updateWidgetState(glanceId, it)
                } ?: run {
                    return Result.failure()
                }
            }
            return Result.success()
        } catch (e: Exception) {
            Log.e("FriendListeningWidgetWorker", e.toString())
            return Result.retry()
        }
    }

    private suspend fun updateWidgetState(glanceId: GlanceId, friend: User) {
        FriendListeningWidget().apply {
            updateAppWidgetState(applicationContext, glanceId) { state ->
                WidgetDataStoreManager.saveFriend(state, friend)
                WidgetDataStoreManager.saveLastUpdated(state, applicationContext)
            }
            update(applicationContext, glanceId)
        }
    }
}