package com.mno.jamscope.features.settings.ui

import android.content.Context
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mno.jamscope.R
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
    onSelectThemeClick: () -> Unit,
    onLogOutClick: () -> Unit,
    onSwitchClick: (String) -> Unit,
    onDeleteAccountClick: (Context) -> Unit,
    onBuyMeACoffeeClick: (Context) -> Unit,
    onBugReportClick: (Context) -> Unit,
    onSuggestFeatureClick: () -> Unit,
    onShowLibrariesClick: () -> Unit,
    onGithubProjectClick: (Context) -> Unit,
) {
    val context = LocalContext.current
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
                onSwitchClick = { onSwitchClick(it) }
            )
            item {
                SettingSectionTitle(R.string.account_setting_tile)
            }
            accountSettingsSection(
                onDeleteAccountClick = { onDeleteAccountClick(context) },
                onLogOutClick = onLogOutClick,
            )
            item {
                SettingSectionTitle(R.string.about_setting_tile)
            }
            aboutSettingsSection(
                onBuyMeACoffeeClick = { onBuyMeACoffeeClick(context) },
                onBugReportClick = { onBugReportClick(context) },
                onSuggestFeatureClick = { onSuggestFeatureClick() },
                onShowLibrariesClick = { onShowLibrariesClick() },
                onGithubProjectClick = { onGithubProjectClick(context) }
            )
        }
    }
}