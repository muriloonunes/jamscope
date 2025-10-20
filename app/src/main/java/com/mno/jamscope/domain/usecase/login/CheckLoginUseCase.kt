package com.mno.jamscope.domain.usecase.login

import com.mno.jamscope.domain.repository.LoginRepository
import javax.inject.Inject

class CheckLoginUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.isUserLoggedIn()
    }
}