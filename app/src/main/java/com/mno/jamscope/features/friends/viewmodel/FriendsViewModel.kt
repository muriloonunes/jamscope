package com.mno.jamscope.features.friends.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mno.jamscope.data.model.Resource
import com.mno.jamscope.data.model.User
import com.mno.jamscope.data.repository.FriendsRepository
import com.mno.jamscope.data.repository.SettingsRepository
import com.mno.jamscope.data.repository.UserRepository
import com.mno.jamscope.features.friends.state.FriendsState
import com.mno.jamscope.ui.navigator.Destination
import com.mno.jamscope.ui.navigator.Navigator
import com.mno.jamscope.ui.theme.AppTheme
import com.mno.jamscope.ui.theme.ThemeAttributes
import com.mno.jamscope.util.LogoutEventBus
import com.mno.jamscope.util.SortingType
import com.mno.jamscope.util.Stuff
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val friendsRepository: FriendsRepository,
    private val navigator: Navigator,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FriendsState())
    val uiState: StateFlow<FriendsState> = _uiState.asStateFlow()

    private var originalFriends: List<User> = emptyList()

    private var lastUpdateTimestamp: Long = 0L

    init {
        // Observe logout and settings toggles
        viewModelScope.launch {
            LogoutEventBus.logoutEvents.collect { resetLastUpdateTimestamp() }
        }
        viewModelScope.launch {
            settingsRepository.getSwitchState("playing_animation_toggle", true)
                .collect { enabled ->
                    _uiState.update { it.copy(playingAnimationEnabled = enabled) }
                }
        }
        viewModelScope.launch {
            settingsRepository.getSwitchState("card_background_color_toggle", true)
                .collect { enabled ->
                    _uiState.update { it.copy(cardBackgroundColorEnabled = enabled) }
                }
        }
        // Restore saved sorting type first
        viewModelScope.launch {
            val saved = friendsRepository.getSortingType()
            _uiState.update { it.copy(sortingType = saved) }
            // After we have sorting type, load data
            loadCachedFriends()
            if (shouldRefresh()) refreshFriends()
        }
    }

    fun onRefresh() {
        if (_uiState.value.isRefreshing) return
        refreshFriends()
    }

    private fun loadCachedFriends() {
        viewModelScope.launch {
            val cachedFriends = friendsRepository.getCachedFriends().firstOrNull()
            originalFriends = if (cachedFriends.isNullOrEmpty()) {
                val friendsDataStore = friendsRepository.getFriendsFromDataStore().first()
                friendsDataStore
            } else {
                cachedFriends
            }
            val sortingType = _uiState.value.sortingType
            val friendsSorted = sortFriends(originalFriends, sortingType)
            _uiState.update {
                it.copy(
                    friends = friendsSorted,
                    recentTracksMap = friendsSorted.associate { f -> f.url to f.recentTracks }
                )
            }
        }
    }

    private fun refreshFriends() {
        _uiState.update { it.copy(isRefreshing = true, errorMessage = "") }
        viewModelScope.launch {
            val userProfile = userRepository.getUserProfile()
            when (val result = userRepository.getUserFriends(userProfile!!.username)) {
                is Resource.Success -> {
                    val friends = result.data
                    userProfile.friends = friends
                    userRepository.saveUserProfile(userProfile)
                    loadRecentTracks(friends)
                    friendsRepository.cacheFriends(friends)
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message, isRefreshing = false) }
                }
            }
            lastUpdateTimestamp = System.currentTimeMillis()
        }
    }

    private fun loadRecentTracks(friends: List<User>) {
        viewModelScope.launch {
            val updatedFriends = friends.map { friend ->
                async {
                    friendsRepository.getRecentTracks(friend)
                    friendsRepository.cacheRecentTracks(
                        friend.url,
                        friend.recentTracks?.track ?: emptyList()
                    )
                    friend
                }
            }.awaitAll()

            // Update base order to the latest data first
            originalFriends = updatedFriends

            _uiState.update { state ->
                val sorted = sortFriends(originalFriends, state.sortingType)
                state.copy(
                    friends = sorted,
                    recentTracksMap = sorted.associate { it.url to it.recentTracks },
                    isRefreshing = false
                )
            }
        }
    }

    fun onSortingTypeChanged(sortingType: SortingType) {
        viewModelScope.launch {
            friendsRepository.saveSortingType(sortingType)
            _uiState.update { state ->
                val sorted = sortFriends(originalFriends, sortingType)
                state.copy(
                    sortingType = sortingType,
                    friends = sorted,
                    recentTracksMap = sorted.associate { it.url to it.recentTracks }
                )
            }
        }
    }

    private fun sortFriends(friends: List<User>, sortingType: SortingType): List<User> {
        return when (sortingType) {
            SortingType.DEFAULT -> friends
            SortingType.ALPHABETICAL -> friends.sortedBy { (it.realname.ifEmpty { it.name })?.lowercase() }
            SortingType.RECENTLY_PLAYED -> friends.sortedWith(
                compareByDescending<User> { it.recentTracks?.track?.firstOrNull()?.dateInfo == null }
                    .thenByDescending { it.recentTracks?.track?.firstOrNull()?.dateInfo?.formattedDate }
            )
        }
    }

    fun shouldRefresh(): Boolean {
        return System.currentTimeMillis() - lastUpdateTimestamp > Stuff.REFRESHING_TIME
    }

    fun getSecondaryContainerColor(name: String?, isDarkTheme: Boolean): Color {
        val colorPalette = if (isDarkTheme) AppTheme.DARK else AppTheme.LIGHT
        return ThemeAttributes.getSecondaryContainerColor(name, colorPalette)
    }

    fun navigateToSettings() {
        viewModelScope.launch {
            navigator.navigate(Destination.SettingsScreen)
        }
    }

    fun showSortingSheet() {
        _uiState.update { it.copy(isBottomSheetShown = true) }
    }

    fun hideSortingSheet() {
        _uiState.update { it.copy(isBottomSheetShown = false) }
    }

    private fun resetLastUpdateTimestamp() {
        lastUpdateTimestamp = 0L
    }
}
