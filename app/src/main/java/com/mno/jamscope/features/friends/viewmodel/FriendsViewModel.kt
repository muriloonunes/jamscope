package com.mno.jamscope.features.friends.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mno.jamscope.data.flows.WidgetIntentBus
import com.mno.jamscope.domain.Resource
import com.mno.jamscope.domain.model.Friend
import com.mno.jamscope.domain.model.Track
import com.mno.jamscope.domain.repository.FriendRepository
import com.mno.jamscope.domain.repository.SettingsRepository
import com.mno.jamscope.domain.usecase.friend.GetFriendRecentTracksUseCase
import com.mno.jamscope.domain.usecase.friend.GetFriendsFromLocalUseCase
import com.mno.jamscope.domain.usecase.friend.SaveFriendsToLocalUseCase
import com.mno.jamscope.domain.usecase.user.GetUserFriendsUseCase
import com.mno.jamscope.domain.usecase.user.GetUserFromLocalUseCase
import com.mno.jamscope.features.friends.ui.SortingType
import com.mno.jamscope.features.settings.domain.model.SwitchState
import com.mno.jamscope.ui.navigator.Destination
import com.mno.jamscope.ui.navigator.Navigator
import com.mno.jamscope.ui.theme.AppTheme
import com.mno.jamscope.ui.theme.ThemeAttributes
import com.mno.jamscope.util.Stuff
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val friendsRepository: FriendRepository,
    private val settingsRepository: SettingsRepository,
    private val navigator: Navigator,
    private val widgetIntentBus: WidgetIntentBus,
    private val getUserFromLocalUseCase: GetUserFromLocalUseCase,
    private val getUserFriendsUseCase: GetUserFriendsUseCase,
    private val getFriendsFromLocalUseCase: GetFriendsFromLocalUseCase,
    private val getFriendRecentTracksUseCase: GetFriendRecentTracksUseCase,
    private val saveFriendsToLocalUseCase: SaveFriendsToLocalUseCase,
) : ViewModel() {
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing
        .onStart {
            loadCachedFriends()
            if (shouldRefresh()) refreshFriends()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _sortingType = MutableStateFlow(SortingType.DEFAULT)
    val sortingType: StateFlow<SortingType> = _sortingType

    private val _friends = MutableStateFlow<List<Friend>>(emptyList())
    val friends: StateFlow<List<Friend>> =
        _friends.combine(sortingType) { friends, sortingType ->
            sortFriends(friends, sortingType)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _recentTracksMap = MutableStateFlow<Map<String, List<Track>>>(emptyMap())
    val recentTracksMap: StateFlow<Map<String, List<Track>>> = friends.map { friendsList ->
        friendsList.associate { it.profileUrl to it.recentTracks }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private val _cardBackgroundColorToggle = MutableStateFlow(true)
    val cardBackgroundColorToggle: StateFlow<Boolean> = _cardBackgroundColorToggle

    private val _playingAnimationToggle = MutableStateFlow(true)
    val playingAnimationToggle: StateFlow<Boolean> = _playingAnimationToggle

    private val _friendToScroll = MutableStateFlow<String?>(null)
    val friendToScroll = _friendToScroll.asStateFlow()

    //talvez é desnecessário usar dois state flow que vao segurar o mesmo valor
    //mas garante que os eventos nao se misturem
    private val _friendToOpen = MutableStateFlow<String?>(null)
    val friendToOpen = _friendToOpen.asStateFlow()

    private var lastUpdateTimestamp: Long = 0L

    init {
        viewModelScope.launch {
            _sortingType.value = friendsRepository.getSortingType()
        }
        viewModelScope.launch {
            combine(
                settingsRepository.getSwitchState("playing_animation_toggle", SwitchState.On),
                settingsRepository.getSwitchState("card_background_color_toggle", SwitchState.On)
            ) { playingAnimation, cardBackground ->
                playingAnimation to cardBackground
            }.collect { (playingAnimation, cardBackground) ->
                _playingAnimationToggle.value = playingAnimation.value
                _cardBackgroundColorToggle.value = cardBackground.value
            }
        }
        viewModelScope.launch {
            widgetIntentBus.widgetClick.collect { name ->
                if (!name.isNullOrEmpty()) {
                    _friendToScroll.value = name
                    _friendToOpen.value = name
                    widgetIntentBus.consume()
                }
            }
        }
    }

    fun onRefresh() {
        if (_isRefreshing.value) return
        refreshFriends()
    }

    private fun loadCachedFriends() {
        viewModelScope.launch {
            val cachedFriends = getFriendsFromLocalUseCase()
            _friends.value = sortFriends(cachedFriends, _sortingType.value)
        }
    }

    private fun refreshFriends() {
        _isRefreshing.value = true
        _errorMessage.value = ""
        viewModelScope.launch {
            val userProfile = getUserFromLocalUseCase()
            when (val result = getUserFriendsUseCase(userProfile.username)) {
                is Resource.Success -> {
                    val friends = result.data //this still has no 'recentTracks' data
                    val friendsWithTracks = fetchAndProcessRecentTracks(friends)
                    _friends.value = friendsWithTracks
                    _recentTracksMap.value =
                        friendsWithTracks.associate { it.profileUrl to it.recentTracks }

                    _isRefreshing.value = false

                    saveFriendsToLocalUseCase(friendsWithTracks)
                }

                is Resource.Error -> {
                    _errorMessage.value = result.message
                    _isRefreshing.value = false
                }
            }
            lastUpdateTimestamp = System.currentTimeMillis()
        }
    }

    private suspend fun fetchAndProcessRecentTracks(friends: List<Friend>): List<Friend> {
        return friends.map { friend ->
            viewModelScope.async(Dispatchers.IO) {
                when (val result = getFriendRecentTracksUseCase(friend.name)) {
                    is Resource.Success -> {
                        friend.copy(recentTracks = result.data)
                    }

                    is Resource.Error -> {
                        Log.e(
                            "FriendsViewModel",
                            "Error fetching tracks for ${friend.name}: ${result.message}"
                        )
                        _errorMessage.value = result.message
                        _isRefreshing.value = false
                        friend
                    }
                }
            }
        }.awaitAll()
    }

    fun onSortingTypeChanged(sortingType: SortingType) {
        viewModelScope.launch {
            _sortingType.value = sortingType
            friendsRepository.saveSortingType(sortingType)
        }
    }

    private fun sortFriends(friends: List<Friend>, sortingType: SortingType): List<Friend> {
        return when (sortingType) {
            SortingType.DEFAULT -> friends
            SortingType.ALPHABETICAL -> friends.sortedBy { (it.realName.ifEmpty { it.name }).lowercase() }
            SortingType.RECENTLY_PLAYED -> friends.sortedWith(
                compareBy<Friend> {
                    val firstTrack = it.recentTracks.firstOrNull()
                    when {
                        firstTrack == null -> 3
                        firstTrack.date == null -> 1
                        else -> 2
                    }
                }.thenByDescending { it.recentTracks.firstOrNull()?.date }
            )
        }
    }

    fun shouldRefresh(): Boolean {
        return System.currentTimeMillis() - lastUpdateTimestamp > Stuff.REFRESHING_TIME
    }

    fun onScrolledToFriend() {
        _friendToScroll.value = null
    }

    fun onFriendOpened() {
        _friendToOpen.value = null
    }

    fun getSecondaryContainerColor(name: String?, isDarkTheme: Boolean): Color {
        val colorPalette =
            if (isDarkTheme) AppTheme.DARK else AppTheme.LIGHT
        return ThemeAttributes.getSecondaryContainerColor(name, colorPalette)
    }

    fun navigateToSettings() {
        viewModelScope.launch {
            navigator.navigate(Destination.SettingsScreen)
        }
    }
}
