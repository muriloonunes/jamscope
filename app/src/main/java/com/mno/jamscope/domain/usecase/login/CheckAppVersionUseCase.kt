package com.mno.jamscope.domain.usecase.login

import com.mno.jamscope.domain.repository.SettingsRepository
import javax.inject.Inject

class CheckAppVersionUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(currentVersion: Int): Boolean {
        val appVersion = settingsRepository.getAppVersion()
        return appVersion < currentVersion
    }
}