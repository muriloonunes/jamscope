package com.mno.jamscope.data.session

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val PROFILE_JSON = stringPreferencesKey("profile_json")
    val SORTING_TYPE = intPreferencesKey("sorting_type")
    val THEME = intPreferencesKey("theme")
    val APP_OPENED = intPreferencesKey("app_opened")
}