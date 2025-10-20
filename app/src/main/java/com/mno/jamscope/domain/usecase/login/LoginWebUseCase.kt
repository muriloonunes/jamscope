package com.mno.jamscope.domain.usecase.login

import com.mno.jamscope.domain.Resource
import com.mno.jamscope.domain.model.User
import com.mno.jamscope.domain.repository.LoginRepository
import javax.inject.Inject

class LoginWebUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(token: String): Resource<User> {
        return repository.login(token)
    }
}