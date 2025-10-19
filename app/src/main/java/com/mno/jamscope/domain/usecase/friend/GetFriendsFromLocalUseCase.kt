package com.mno.jamscope.domain.usecase.friend

import com.mno.jamscope.domain.model.Friend
import com.mno.jamscope.domain.repository.FriendRepository
import javax.inject.Inject

class GetFriendsFromLocalUseCase @Inject constructor(
    private val friendRepository: FriendRepository,
) {
    suspend operator fun invoke(): List<Friend> {
        return friendRepository.getFriends()
    }
}