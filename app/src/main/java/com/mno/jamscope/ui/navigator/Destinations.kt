package com.mno.jamscope.ui.navigator

import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable
    data object LoginRoute : Destination
    @Serializable
    data object LoginScreenNative : Destination
    @Serializable
    data object LoginScreenWeb : Destination
    @Serializable
    data object LastFmWebLoginScreen : Destination
    @Serializable
    data object AppRoute : Destination
    @Serializable
    data object FriendsScreen : Destination
    @Serializable
    data object ProfileScreen : Destination
    @Serializable
    data object SettingsScreen : Destination
    @Serializable
    data object SuggestFeatureScreen : Destination
    @Serializable
    data class LibrariesLicenseScreen(val screenType: ScreenType) : Destination
    @Serializable
    data object AboutScreen : Destination
}

@Serializable
enum class ScreenType {
    LIBRARIES,
    LICENSE
}
