package com.mno.jamscope.domain.usecase

import com.mno.jamscope.domain.repository.FriendRepository
import com.mno.jamscope.domain.repository.LoginRepository
import com.mno.jamscope.domain.repository.SettingsRepository
import com.mno.jamscope.domain.repository.UserRepository
import com.mno.jamscope.domain.usecase.login.CheckLoginUseCase
import com.mno.jamscope.domain.usecase.login.LoginUserUseCase
import com.mno.jamscope.domain.usecase.login.LoginWebUseCase
import com.mno.jamscope.domain.usecase.user.LogoutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun provideCheckLoginUseCase(
        loginRepository: LoginRepository,
    ): CheckLoginUseCase {
        return CheckLoginUseCase(
            repository = loginRepository
        )
    }

    @Provides
    @Singleton
    fun provideLoginUserUseCase(
        loginRepository: LoginRepository,
    ): LoginUserUseCase {
        return LoginUserUseCase(
            repository = loginRepository
        )
    }

    @Provides
    @Singleton
    fun provideLoginWebUseCase(
        loginRepository: LoginRepository,
    ): LoginWebUseCase {
        return LoginWebUseCase(
            repository = loginRepository
        )
    }

    @Provides
    @Singleton
    fun provideLogOutUseCase(
        userRepository: UserRepository,
        settingsRepository: SettingsRepository,
        friendRepository: FriendRepository,
    ): LogoutUseCase {
        return LogoutUseCase(
            userRepository = userRepository,
            settingsRepository = settingsRepository,
            friendRepository = friendRepository
        )
    }
}