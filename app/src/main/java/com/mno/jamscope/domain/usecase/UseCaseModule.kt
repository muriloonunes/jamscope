package com.mno.jamscope.domain.usecase

import com.mno.jamscope.domain.repository.LoginRepository
import com.mno.jamscope.domain.usecase.login.CheckLoginUseCase
import com.mno.jamscope.domain.usecase.login.LoginUserUseCase
import com.mno.jamscope.domain.usecase.login.LoginWebUseCase
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
}