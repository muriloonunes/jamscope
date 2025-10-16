package com.mno.jamscope.features.widgets

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mno.jamscope.domain.model.Friend
import kotlinx.serialization.json.Json

object WidgetDataStoreManager {
    private const val WIDGET_KEY = "widget_friend_key"
    private const val LAST_UPDATED_KEY = "widget_updated_key"

    fun saveFriend(prefs: MutablePreferences, friend: Friend) {
        val jsonFriend = Json.encodeToString(friend)
        prefs[stringPreferencesKey(WIDGET_KEY)] = jsonFriend
    }

    fun getFriend(prefs: Preferences): Friend? {
        val jsonFriend = prefs[stringPreferencesKey(WIDGET_KEY)] ?: ""
        return if (jsonFriend.isEmpty()) {
            null
        } else {
            Json.decodeFromString<Friend>(jsonFriend)
        }
    }

    fun saveLastUpdated(prefs: MutablePreferences, formattedDate: String) {
        prefs[stringPreferencesKey(LAST_UPDATED_KEY)] = formattedDate
    }

    fun getLastUpdated(prefs: Preferences): String {
        return prefs[stringPreferencesKey(LAST_UPDATED_KEY)] ?: ""
    }

    fun saveFriendsGroup(prefs: MutablePreferences, friends: List<Friend>) {
        val jsonFriends = Json.encodeToString(friends)
        prefs[stringPreferencesKey(WIDGET_KEY)] = jsonFriends
    }

    fun getFriendsGroup(prefs: Preferences): List<Friend> {
        val jsonFriends = prefs[stringPreferencesKey(WIDGET_KEY)] ?: ""
        return if (jsonFriends.isEmpty()) {
            emptyList()
        } else {
            Json.decodeFromString<List<Friend>>(jsonFriends)
        }
    }
}