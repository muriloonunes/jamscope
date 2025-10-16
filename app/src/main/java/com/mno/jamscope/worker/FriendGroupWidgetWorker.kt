package com.mno.jamscope.worker

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mno.jamscope.data.repository.FriendsRepository
import com.mno.jamscope.domain.model.Friend
import com.mno.jamscope.features.widgets.WidgetDataStoreManager
import com.mno.jamscope.features.widgets.friendgroup.FriendGroupWidget
import com.mno.jamscope.features.widgets.singlefriend.generateLastUpdatedString

class FriendGroupWidgetWorker(
    private val appContext: Context,
    workerParams: WorkerParameters,
    var repository: FriendsRepository,
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

    private suspend fun updateWidgetState(glanceId: GlanceId, friendsList: List<Friend>) {
        FriendGroupWidget().apply {
            updateAppWidgetState(applicationContext, glanceId) { state ->
                WidgetDataStoreManager.saveFriendsGroup(state, friendsList)
                val formattedDate = generateLastUpdatedString(applicationContext)
                WidgetDataStoreManager.saveLastUpdated(state, formattedDate)
            }
            update(applicationContext, glanceId)
        }
    }
}