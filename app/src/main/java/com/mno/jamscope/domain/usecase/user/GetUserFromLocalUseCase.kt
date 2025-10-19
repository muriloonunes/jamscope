package com.mno.jamscope.domain.usecase.user

import com.mno.jamscope.domain.model.User
import com.mno.jamscope.domain.repository.UserRepository
import javax.inject.Inject

class GetUserFromLocalUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): User {
        return userRepository.getUser()
    }
}