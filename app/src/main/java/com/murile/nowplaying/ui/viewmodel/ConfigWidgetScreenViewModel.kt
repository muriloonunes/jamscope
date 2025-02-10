package com.murile.nowplaying.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murile.nowplaying.data.model.User
import com.murile.nowplaying.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigWidgetScreenViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _friends = MutableLiveData<List<User>>()
    val friends: LiveData<List<User>> = _friends

    init {
        viewModelScope.launch {
            val profile = userRepository.getUserProfile()
            if (profile != null) {
                _friends.value = profile.friends?.sortedBy {
                    it.realname.ifEmpty { it.name }?.lowercase()
                } ?: emptyList()
            }
        }
    }
}