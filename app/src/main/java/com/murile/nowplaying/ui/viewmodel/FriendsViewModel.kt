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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _friendsDefault = MutableStateFlow<List<User>>(emptyList())
    val friendsDefault: StateFlow<List<User>> = _friendsDefault

    private val _friendsRecently = MutableStateFlow<List<User>>(emptyList())
    val friendsRecently: StateFlow<List<User>> = _friendsRecently

    private val _friendsAlphabetical = MutableStateFlow<List<User>>(emptyList())
    val friendsAlphabetical: StateFlow<List<User>> = _friendsAlphabetical

    private val _recentTracksMap = MutableStateFlow<Map<String, RecentTracks?>>(emptyMap())
    val recentTracksMap: StateFlow<Map<String, RecentTracks?>> = _recentTracksMap

    private val _sortingType = MutableStateFlow(SortingType.DEFAULT)
    val sortingType: StateFlow<SortingType> = _sortingType

    private var lastUpdateTimestamp: Long = 0L

    init {
        viewModelScope.launch {
            _sortingType.value = userRepository.getSortingType()
        }
    }

    fun onRefresh() {
        if (_isRefreshing.value) {
            return
        }
        _isRefreshing.value = true
        _errorMessage.value = ""
        viewModelScope.launch {
            val userProfile = userRepository.getUserProfile()
            delay(1000)
            when (val result = userRepository.getUserFriends(userProfile!!.username)) {
                is Resource.Success -> {
                    val friends = result.data
                    userRepository.saveUserProfile(userProfile)
                    _friendsDefault.value = friends
                    loadRecentTracks(friends)
                    _isRefreshing.value = false
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
            friends.forEach { friend ->
                userRepository.getRecentTracks(friend)
                _recentTracksMap.update { oldMap ->
                    oldMap + (friend.url to friend.recentTracks)
                }
            }
            sortFriendsList()
        }
    }

    private fun sortFriendsList() {
        _friendsAlphabetical.value = _friendsDefault.value.sortedBy {
            (it.realname.ifEmpty { it.name })?.lowercase()
        }

        _friendsRecently.value = _friendsDefault.value.sortedWith(
            compareByDescending<User> { it.recentTracks?.track?.firstOrNull()?.dateInfo == null }
                .thenByDescending { it.recentTracks?.track?.firstOrNull()?.dateInfo?.formattedDate })
    }

    fun onSortingTypeChanged(sortingType: SortingType) {
//        viewModelScope.launch {
//            userRepository.saveSortingType(sortingType)
//        }
        _sortingType.value = sortingType
        onRefresh()
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