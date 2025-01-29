package com.murile.nowplaying.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murile.nowplaying.data.api.Resource
import com.murile.nowplaying.data.model.RecentTracks
import com.murile.nowplaying.data.model.User
import com.murile.nowplaying.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.ZoneOffset
import javax.inject.Inject


@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing get() = _isRefreshing.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _friends = MutableStateFlow<List<User>>(emptyList())
    val friends: StateFlow<List<User>> = _friends

    private val _recentTracksMap = MutableStateFlow<Map<String, RecentTracks?>>(emptyMap())
    val recentTracksMap: StateFlow<Map<String, RecentTracks?>> = _recentTracksMap

    private var lastUpdateTimestamp: Long = 0L

    fun onRefresh() {
        _isRefreshing.value = true
        _errorMessage.value = ""
        viewModelScope.launch {
            val userProfile = userRepository.getUserProfile()
            delay(1000)
            when (val result = userRepository.getUserFriends(userProfile!!.username)) {
                is Resource.Success -> {
                    val friends = result.data
                    userProfile.friends = friends
                    userRepository.saveUserProfile(userProfile)
                    _friends.value = friends
                    _isRefreshing.value = false
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
            friends.forEach { friend ->
                userRepository.getRecentTracks(friend)
                _recentTracksMap.update { oldMap ->
                    oldMap + (friend.url to friend.recentTracks)
                }
            }
        }
    }

    fun shouldRefresh(): Boolean {
        return System.currentTimeMillis() - lastUpdateTimestamp > 15000000 // 2 min e meio em milisegundos
    }

    fun resetLastUpdateTimestamp() {
        lastUpdateTimestamp = 0L
    }
}

internal object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val sampleTime = "12:30"
        val offsetTime = LocalTime.parse(sampleTime)
            .atOffset(ZoneOffset.UTC)
            .withOffsetSameInstant(ZoneOffset.of("+03:00"))
        println(offsetTime)

        // Getting LocalTine from OffsetTime
        val result = offsetTime.toLocalTime()
        println(result)
    }
}