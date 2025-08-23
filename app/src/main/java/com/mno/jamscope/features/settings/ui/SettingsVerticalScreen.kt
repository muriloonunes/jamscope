package com.mno.jamscope.features.settings.ui

import android.content.Context
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mno.jamscope.R
import com.mno.jamscope.features.settings.viewmodel.SettingsViewModel
import com.mno.jamscope.ui.components.SettingSectionTitle
import com.mno.jamscope.ui.components.aboutSettingsSection
import com.mno.jamscope.ui.components.accountSettingsSection
import com.mno.jamscope.ui.components.personalizationSettingsSection
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
fun SettingsVerticalScreen(
    modifier: Modifier = Modifier,
    themePreference: Int,
    switchStates: Map<String, Boolean>,
    settingsViewModel: SettingsViewModel,
    context: Context,
    onSelectThemeClick: () -> Unit,
    onLogOutClick: () -> Unit
) {
    LazyColumnScrollbar(
        modifier = modifier,
        state = rememberLazyListState(),
        settings = ScrollbarSettings(
            thumbUnselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
            thumbSelectedColor = MaterialTheme.colorScheme.onSurface,
            scrollbarPadding = 2.dp
        )
    ) {
        LazyColumn {
            item {
                SettingSectionTitle(R.string.personalization_setting_title)
            }
            personalizationSettingsSection(
                themePreference = themePreference,
                onSelectThemeClick = onSelectThemeClick,
                switchStates = switchStates,
                onSwitchClick = { settingsViewModel.toggleSwitch(it) }
            )
            item {
                SettingSectionTitle(R.string.account_setting_tile)
            }
            accountSettingsSection(
                onDeleteAccountClick = { settingsViewModel.openDeleteAccount(context) },
                onLogOutClick = onLogOutClick,
            )
            item {
                SettingSectionTitle(R.string.about_setting_tile)
            }
            aboutSettingsSection(
                onBuyMeACoffeeClick = { settingsViewModel.openBuyMeACoffee(context) },
                onBugReportClick = { settingsViewModel.sendBugReportMail(context) },
                onSuggestFeatureClick = { settingsViewModel.navigateToWebView() },
                onShowLibrariesClick = { settingsViewModel.navigateToLibraries() },
                onGithubProjectClick = { settingsViewModel.openGithubProject(context) }
            )
        }
    }
}