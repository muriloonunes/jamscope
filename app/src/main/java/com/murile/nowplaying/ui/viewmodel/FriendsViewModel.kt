package com.murile.nowplaying.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murile.nowplaying.data.model.RecentTracks
import com.murile.nowplaying.data.model.Resource
import com.murile.nowplaying.data.model.User
import com.murile.nowplaying.data.repository.UserRepository
import com.murile.nowplaying.ui.theme.ThemeAttributes
import com.murile.nowplaying.util.SortingType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing get() = _isRefreshing.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _sortingType = MutableStateFlow(SortingType.DEFAULT)
    val sortingType: StateFlow<SortingType> = _sortingType

    private val _friends = MutableStateFlow<List<User>>(emptyList())
    val friends: StateFlow<List<User>> =
        _friends.combine(sortingType) { friends, sortingType ->  // Combine friends and sortingType
            when (sortingType) {
                SortingType.DEFAULT -> friends
                SortingType.ALPHABETICAL -> friends.sortedBy {
                    (it.realname.ifEmpty { it.name })?.lowercase()
                }

                SortingType.RECENTLY_PLAYED -> friends.sortedWith(
                    compareByDescending<User> { it.recentTracks?.track?.firstOrNull()?.dateInfo == null }
                        .thenByDescending { it.recentTracks?.track?.firstOrNull()?.dateInfo?.formattedDate }) // Use derived property
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _recentTracksMap = MutableStateFlow<Map<String, RecentTracks?>>(emptyMap())
    val recentTracksMap: StateFlow<Map<String, RecentTracks?>> = _recentTracksMap

    private var lastUpdateTimestamp: Long = 0L

    init {
        viewModelScope.launch {
            _sortingType.value = userRepository.getSortingType()
        }
    }

    fun onRefresh() {
        if (_isRefreshing.value) return
        refreshFriends()
    }

    private fun refreshFriends() {
        _isRefreshing.value = true
        _errorMessage.value = ""
        viewModelScope.launch {
            val userProfile = userRepository.getUserProfile()
            delay(1000)
            when (val result = userRepository.getUserFriends(userProfile!!.username)) {
                is Resource.Success -> {
                    val friends = result.data
                    userRepository.saveUserProfile(userProfile)
                    loadRecentTracks(friends)
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
            friends.map { friend ->
                async {
                    userRepository.getRecentTracks(friend)
                    friend
                }
            }.awaitAll()
                .let { updatedFriends ->
                    _friends.value = updatedFriends
                    _recentTracksMap.value = updatedFriends.associate { it.url to it.recentTracks }
                }
            _isRefreshing.value = false
        }
    }

    fun onSortingTypeChanged(sortingType: SortingType) {
        viewModelScope.launch {
            userRepository.saveSortingType(sortingType)
        }
        _sortingType.value = sortingType
    }

    fun shouldRefresh(): Boolean {
        return System.currentTimeMillis() - lastUpdateTimestamp > 15000000 // 2 min e meio em milisegundos
    }

    fun resetLastUpdateTimestamp() {
        lastUpdateTimestamp = 0L
    }

    fun getSecondaryContainerColor(name: String?, isDarkTheme: Boolean): Color {
        val colorPalette =
            if (isDarkTheme) ThemeAttributes.ColorPalette.DARK else ThemeAttributes.ColorPalette.LIGHT
        return ThemeAttributes.getSecondaryContainerColor(name, colorPalette)
    }
}