package com.mno.jamscope.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mno.jamscope.R
import com.mno.jamscope.data.model.Profile
import com.mno.jamscope.data.model.Session
import com.mno.jamscope.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private companion object {
        const val RETRY_COUNT = 10
    }

    private val _isProfileUpdated = MutableStateFlow(false)
    val isProfileUpdated = _isProfileUpdated
        .onStart {
            loadCachedProfile()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _userProfile = MutableStateFlow<Profile?>(null)
    val userProfile: StateFlow<Profile?> = _userProfile

    private fun loadCachedProfile(retryCount: Int = 0) {
        viewModelScope.launch {
            try {
                val userProfile = userRepository.getCachedUserProfile()
                _userProfile.value = userProfile
            } catch (e: IllegalStateException) {
                if (retryCount < RETRY_COUNT) {
                    val delayTime = calculateExponentialDelay(retryCount)
                    loadDefaultProfile()
                    delay(delayTime)
                    loadCachedProfile(retryCount + 1)
                } else {
                    return@launch
                }
            }
        }
    }

    private fun loadDefaultProfile() {
        _userProfile.value = Profile(
            username = context.getString(R.string.user),
            senha = "1234",
            session = Session("1234"),
            subscriber = 0,
            imageUrl = Uri.parse("android.resource//${context.packageName}/${R.drawable.profile_pic_placeholder}").toString()
        )
    }

    private fun calculateExponentialDelay(retryCount: Int): Long {
        val baseDelay = 1000L // 1 segundo como base
        return baseDelay * (2.0.pow(retryCount)).toLong()
    }
}