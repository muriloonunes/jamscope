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
import com.mno.jamscope.features.widgets.singlefriend.FriendListeningWidget
import com.mno.jamscope.features.widgets.singlefriend.generateLastUpdatedString

class FriendListeningWidgetWorker(
    private val appContext: Context,
    workerParams: WorkerParameters,
    private val getFriendRecentTracksUseCase: GetFriendRecentTracksUseCase,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val glanceIds = GlanceAppWidgetManager(appContext)
            .getGlanceIds(FriendListeningWidget::class.java)

        if (glanceIds.isEmpty()) return Result.failure()
        try {
            for (glanceId in glanceIds) {
                val prefs =
                    FriendListeningWidget().getAppWidgetState<Preferences>(appContext, glanceId)
                val friend = WidgetDataStoreManager.getFriend(prefs)

                if (friend == null) {
                    Log.e(
                        "FriendListeningWidgetWorker",
                        "No friend found in widget state for glanceId: $glanceId"
                    )
                    continue
                }

                val updatedFriend = when (val result = getFriendRecentTracksUseCase(friend.name)) {
                    is Resource.Success -> {
                        friend.copy(recentTracks = result.data)
                    }

                    is Resource.Error -> {
                        Log.e("FriendListeningWidgetWorker", "Erro: ${result.message}")
                        continue
                    }
                }
                updateWidgetState(glanceId, updatedFriend)
            }
            return Result.success()
        } catch (e: Exception) {
            Log.e("FriendListeningWidgetWorker", e.toString())
            return Result.retry()
        }
    }

    private suspend fun updateWidgetState(glanceId: GlanceId, friend: Friend) {
        FriendListeningWidget().apply {
            updateAppWidgetState(applicationContext, glanceId) { state ->
                WidgetDataStoreManager.saveFriend(state, friend)
                val formattedDate = generateLastUpdatedString(applicationContext)
                WidgetDataStoreManager.saveLastUpdated(state, formattedDate)
            }
            update(applicationContext, glanceId)
        }
    }
}