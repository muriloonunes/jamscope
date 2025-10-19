package com.mno.jamscope.domain.usecase.user

import com.mno.jamscope.domain.Resource
import com.mno.jamscope.domain.model.User
import com.mno.jamscope.domain.repository.UserRepository
import javax.inject.Inject

class GetUserInfoFromApiUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(username: String): Resource<User> {
        return repository.getUserInfoFromApi(username)
    }
}
