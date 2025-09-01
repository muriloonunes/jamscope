package com.mno.jamscope.features.friends.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mno.jamscope.data.flows.LogoutEventBus
import com.mno.jamscope.data.flows.WidgetIntentBus
import com.mno.jamscope.data.model.RecentTracks
import com.mno.jamscope.data.model.Resource
import com.mno.jamscope.data.model.User
import com.mno.jamscope.data.repository.FriendsRepository
import com.mno.jamscope.data.repository.SettingsRepository
import com.mno.jamscope.data.repository.UserRepository
import com.mno.jamscope.ui.navigator.Destination
import com.mno.jamscope.ui.navigator.Navigator
import com.mno.jamscope.ui.theme.AppTheme
import com.mno.jamscope.ui.theme.ThemeAttributes
import com.mno.jamscope.util.SortingType
import com.mno.jamscope.util.Stuff
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val friendsRepository: FriendsRepository,
    private val navigator: Navigator,
    private val settingsRepository: SettingsRepository,
    private val logoutBus: LogoutEventBus,
    private val widgetIntentBus: WidgetIntentBus,
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

    private val _friends = MutableStateFlow<List<User>>(emptyList())
    val friends: StateFlow<List<User>> =
        _friends.combine(sortingType) { friends, sortingType ->
            sortFriends(friends, sortingType)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _recentTracksMap = MutableStateFlow<Map<String, RecentTracks?>>(emptyMap())
    val recentTracksMap: StateFlow<Map<String, RecentTracks?>> = friends.map { friendsList ->
        friendsList.associate { it.url to it.recentTracks }
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
            logoutBus.logoutEvents.collect {
                resetLastUpdateTimestamp()
            }
        }
        viewModelScope.launch {
            _sortingType.value = friendsRepository.getSortingType()
        }
        viewModelScope.launch {
            combine(
                settingsRepository.getSwitchState("playing_animation_toggle", true),
                settingsRepository.getSwitchState("card_background_color_toggle", true)
            ) { playingAnimation, cardBackground ->
                playingAnimation to cardBackground
            }.collect { (playingAnimation, cardBackground) ->
                _playingAnimationToggle.value = playingAnimation
                _cardBackgroundColorToggle.value = cardBackground
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
            val cachedFriends = friendsRepository.getCachedFriends().firstOrNull()
            if (cachedFriends.isNullOrEmpty()) {
                val friendsDataStore = friendsRepository.getFriendsFromDataStore().first()
                _friends.value = sortFriends(friendsDataStore, _sortingType.value)
            } else {
                _friends.value = sortFriends(cachedFriends, _sortingType.value)
            }
        }
    }

    private fun refreshFriends() {
        _isRefreshing.value = true
        _errorMessage.value = ""
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
                    _errorMessage.value = result.message
                    _isRefreshing.value = false
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

            _friends.value = updatedFriends
            _recentTracksMap.value = updatedFriends.associate { it.url to it.recentTracks }
            _isRefreshing.value = false
        }
    }

    fun onSortingTypeChanged(sortingType: SortingType) {
        viewModelScope.launch {
            friendsRepository.saveSortingType(sortingType)
            _sortingType.value = sortingType
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

    private fun resetLastUpdateTimestamp() {
        lastUpdateTimestamp = 0L
    }
}
