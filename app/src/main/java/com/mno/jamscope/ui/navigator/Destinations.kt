package com.mno.jamscope.ui.navigator

import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable
    data object LoginRoute : Destination
    @Serializable
    data object LoginScreen : Destination
    @Serializable
    data object AppRoute : Destination
    @Serializable
    data object FriendsScreen : Destination
    @Serializable
    data object ProfileScreen : Destination
    @Serializable
    data object SettingsScreen : Destination
    @Serializable
    data object WebViewScreen : Destination

    @Serializable
    data object LibrariesScreen : Destination
}
