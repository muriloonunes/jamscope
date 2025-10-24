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
import com.mno.jamscope.domain.Resource
import com.mno.jamscope.domain.model.Friend
import com.mno.jamscope.domain.usecase.friend.GetFriendRecentTracksUseCase
import com.mno.jamscope.features.widgets.WidgetDataStoreManager
import com.mno.jamscope.features.widgets.friendgroup.FriendGroupWidget
import com.mno.jamscope.features.widgets.singlefriend.generateLastUpdatedString
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class FriendGroupWidgetWorker(
    private val appContext: Context,
    workerParams: WorkerParameters,
    private val getFriendRecentTracksUseCase: GetFriendRecentTracksUseCase,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val glanceIds = GlanceAppWidgetManager(appContext)
            .getGlanceIds(FriendGroupWidget::class.java)

        if (glanceIds.isEmpty()) return Result.failure()
        try {
            for (glanceId in glanceIds) {
                val prefs = FriendGroupWidget().getAppWidgetState<Preferences>(appContext, glanceId)
                val friendsList = WidgetDataStoreManager.getFriendsGroup(prefs)

                if (friendsList.isEmpty()) {
                    Log.e("FriendGroupWidgetWorker", "No friends found for glanceId: $glanceId")
                    continue
                }

                val updatedFriends = coroutineScope {
                    friendsList.map { friend ->
                        async {
                            when (val result = getFriendRecentTracksUseCase(friend.name)) {
                                is Resource.Success -> friend.copy(recentTracks = result.data)
                                is Resource.Error -> {
                                    Log.e(
                                        "FriendGroupWidgetWorker",
                                        "Error updating ${friend.name}: ${result.message}"
                                    )
                                    friend
                                }
                            }
                        }
                    }.awaitAll()
                }

                updateWidgetState(glanceId, updatedFriends)
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