package com.murile.nowplaying.worker

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.murile.nowplaying.data.model.User
import com.murile.nowplaying.data.repository.FriendsRepository
import com.murile.nowplaying.widget.WidgetDataStoreManager
import com.murile.nowplaying.widget.friendgroup.FriendGroupWidget

class FriendGroupWidgetWorker(
    private val appContext: Context,
    workerParams: WorkerParameters,
    var repository: FriendsRepository
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val glanceIds = GlanceAppWidgetManager(appContext)
            .getGlanceIds(FriendGroupWidget::class.java)

        if (glanceIds.isEmpty()) return Result.failure()
        try {
            for (glanceId in glanceIds) {
                val prefs = FriendGroupWidget().getAppWidgetState<Preferences>(appContext, glanceId)
                val friendsList = WidgetDataStoreManager.getFriendsGroup(prefs)

                if (friendsList.isNotEmpty()) {
                    val updatedFriends = friendsList.map { friend ->
                        repository.getRecentTracks(friend)
                        friend
                    }

                    updateWidgetState(glanceId, updatedFriends)
                } else {
                    return Result.failure()
                }
            }

            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }
    }

    private suspend fun updateWidgetState(glanceId: GlanceId, friendsList: List<User>) {
        FriendGroupWidget().apply {
            updateAppWidgetState(appContext, glanceId) { state ->
                WidgetDataStoreManager.saveFriendsGroup(state, friendsList)
                WidgetDataStoreManager.saveLastUpdated(state)
            }
            update(appContext, glanceId)
        }
    }
}