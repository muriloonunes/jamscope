package com.mno.jamscope.features.settings.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mno.jamscope.features.settings.ui.components.aboutSettingsSection
import com.mno.jamscope.features.settings.ui.components.accountSettingsSection
import com.mno.jamscope.features.settings.ui.components.personalizationSettingsSection
import com.mno.jamscope.ui.theme.NowPlayingTheme

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
    onAboutClick: () -> Unit,
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        personalizationSettingsSection(
            themePreference = themePreference,
            onSelectThemeClick = onSelectThemeClick,
            switchStates = switchStates,
            onSwitchClick = { onSwitchClick(it) }
        )
        accountSettingsSection(
            onDeleteAccountClick = { onDeleteAccountClick(context) },
            onLogOutClick = onLogOutClick,
        )
        aboutSettingsSection(
            onBuyMeACoffeeClick = { onBuyMeACoffeeClick(context) },
            onBugReportClick = { onBugReportClick(context) },
            onSuggestFeatureClick = { onSuggestFeatureClick() },
            onShowLibrariesClick = { onShowLibrariesClick() },
            onAboutClick = { onAboutClick() }
        )
    }
}

@Preview
@Composable
private fun SettingsVerticalScreenPreview() {
    NowPlayingTheme(2) {
        SettingsVerticalScreen(
            themePreference = 0,
            switchStates = mapOf(
                "switch1" to true,
                "switch2" to false
            ),
            onSelectThemeClick = {},
            onLogOutClick = {},
            onSwitchClick = {},
            onDeleteAccountClick = {},
            onBuyMeACoffeeClick = {},
            onBugReportClick = {},
            onSuggestFeatureClick = {},
            onShowLibrariesClick = {},
            onAboutClick = {}
        )
    }
}


