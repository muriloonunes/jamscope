package com.mno.jamscope.domain.usecase.friend

import com.mno.jamscope.domain.model.Friend
import com.mno.jamscope.domain.repository.FriendRepository
import javax.inject.Inject

class SaveFriendsToLocalUseCase @Inject constructor(
    private val friendsRepository: FriendRepository
) {
    suspend operator fun invoke(friends: List<Friend>) {
        friendsRepository.saveFriends(friends)
    }
}