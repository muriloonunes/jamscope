package com.murile.nowplaying.widget

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.murile.nowplaying.data.model.User
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object WidgetDataStoreManager {
    private const val WIDGET_KEY = "widget_friend_key"
    private const val LAST_UPDATED_KEY = "widget_updated_key"

    fun saveFriend(prefs: MutablePreferences, friend: User) {
        val jsonFriend = Json.encodeToString(friend)
        prefs[stringPreferencesKey(WIDGET_KEY)] = jsonFriend
    }

    fun getFriend(prefs: Preferences): User? {
        val jsonFriend = prefs[stringPreferencesKey(WIDGET_KEY)] ?: ""
        return if (jsonFriend.isEmpty()) {
            null
        } else {
            Json.decodeFromString<User>(jsonFriend)
        }
    }

    fun saveLastUpdated(prefs: MutablePreferences) {
        val formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM HH:mm:ss", Locale.getDefault()))
        prefs[stringPreferencesKey(LAST_UPDATED_KEY)] = formattedDate
    }

    fun getLastUpdated(prefs: Preferences): String {
        return prefs[stringPreferencesKey(LAST_UPDATED_KEY)] ?: ""
    }

    fun saveFriendsGroup(prefs: MutablePreferences, friends: List<User>) {
        val jsonFriends = Json.encodeToString(friends)
        prefs[stringPreferencesKey(WIDGET_KEY)] = jsonFriends
    }

    fun getFriendsGroup(prefs: Preferences): List<User> {
        val jsonFriends = prefs[stringPreferencesKey(WIDGET_KEY)] ?: ""
        return if (jsonFriends.isEmpty()) {
            emptyList()
        } else {
            Json.decodeFromString<List<User>>(jsonFriends)
        }
    }
}