package com.mno.jamscope.domain.usecase.user

import com.mno.jamscope.domain.repository.FriendRepository
import com.mno.jamscope.domain.repository.SettingsRepository
import com.mno.jamscope.domain.repository.UserRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
    private val friendRepository: FriendRepository,
) {
    suspend operator fun invoke() {
        userRepository.clearUserSession()
        settingsRepository.clearUserData()
        friendRepository.clearFriends()
    }
}