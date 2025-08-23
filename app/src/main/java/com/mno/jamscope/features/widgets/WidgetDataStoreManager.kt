package com.mno.jamscope.features.widgets

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mno.jamscope.data.model.User
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
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

    fun saveLastUpdated(prefs: MutablePreferences, context: Context) {
        val locale = Locale.getDefault()
        val dateTimeFormatter: DateTimeFormatter
        val is24HourFormat = android.text.format.DateFormat.is24HourFormat(context)
        val dateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT, locale)
        val datePattern = (dateFormat as SimpleDateFormat).toPattern()
        dateTimeFormatter = if (is24HourFormat) {
            DateTimeFormatter.ofPattern("$datePattern HH:mm", locale)
        } else {
            DateTimeFormatter.ofPattern("$datePattern hh:mm a", locale)
        }
        val formattedDate = LocalDateTime.now().format(dateTimeFormatter)
//        val formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM HH:mm:ss", Locale.getDefault()))
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