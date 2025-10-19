package com.mno.jamscope.domain.usecase.user

import com.mno.jamscope.domain.model.User
import com.mno.jamscope.domain.repository.UserRepository
import javax.inject.Inject

class SaveUserDataUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(user: User) {
        userRepository.saveUser(user)
    }
}