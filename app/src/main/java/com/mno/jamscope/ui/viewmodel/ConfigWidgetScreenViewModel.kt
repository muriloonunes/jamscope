package com.mno.jamscope.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mno.jamscope.domain.model.Friend
import com.mno.jamscope.domain.usecase.friend.GetFriendsFromLocalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigWidgetScreenViewModel @Inject constructor(
    private val getFriendsFromLocalUseCase: GetFriendsFromLocalUseCase,
) : ViewModel() {
    private val _friends = MutableLiveData<List<Friend>>()
    val friends: LiveData<List<Friend>> = _friends

    init {
        viewModelScope.launch {
            _friends.value = getFriendsFromLocalUseCase().sortedBy {
                it.realName.ifEmpty { it.name }.lowercase()
            }
        }
    }
}