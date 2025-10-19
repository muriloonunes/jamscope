package com.mno.jamscope.domain.usecase.login

import com.mno.jamscope.domain.repository.LoginRepository
import javax.inject.Inject

class CheckLoginUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(
        username: String,
        password: String,
        sessionKey: String
    ): Boolean {
        return repository.isUserLoggedIn() && repository.isStillAuthenticated(username, password, sessionKey)
    }
}