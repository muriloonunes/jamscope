package com.mno.jamscope.domain.usecase.user

import com.mno.jamscope.domain.Resource
import com.mno.jamscope.domain.model.Friend
import com.mno.jamscope.domain.repository.UserRepository
import javax.inject.Inject

class GetUserFriendsUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(username: String): Resource<List<Friend>> {
        return repository.getUserFriends(username)
    }
}
