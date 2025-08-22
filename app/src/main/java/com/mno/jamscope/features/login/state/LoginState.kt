package com.mno.jamscope.features.login.state

data class LoginState(
    val username: String = "",
    val password: String = "",
    val errorMessage: String = "",
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false,
)