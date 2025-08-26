package com.mno.jamscope.features.settings.ui

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.mno.jamscope.R
import com.mno.jamscope.features.settings.state.SettingsUiState
import com.mno.jamscope.ui.theme.LocalWindowSizeClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTela(
    uiState: SettingsUiState,
    onTileSelected: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    onSelectThemeClick: () -> Unit,
    onLogOutClick: () -> Unit,
    hideThemeDialog: () -> Unit,
    hideLogOutDialog: () -> Unit,
    setThemePreference: (Int) -> Unit,
    onLogoutDialogClick: () -> Unit,
    onSwitchClick: (String) -> Unit,
    onDeleteAccountClick: (Context) -> Unit,
    onBuyMeACoffeeClick: (Context) -> Unit,
    onBugReportClick: (Context) -> Unit,
    onSuggestFeatureClick: () -> Unit,
    onShowLibrariesClick: () -> Unit,
    onGithubProjectClick: (Context) -> Unit,
) {
    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior()
    val themePreference = uiState.themePreference
    val switchStates = uiState.switchStates
    val selectedTile = uiState.selectedTile
    val windowSizeClass = LocalWindowSizeClass.current

    val showLogOutDialog = uiState.showLogOutDialog
    val showThemeDialog = uiState.showThemeDialog
    val windowWidth = windowSizeClass.windowWidthSizeClass

    val sectionTiles = listOf(
        R.string.personalization_setting_title,
        R.string.account_setting_tile,
        R.string.about_setting_tile
    )

    when (windowWidth) {
        WindowWidthSizeClass.COMPACT -> {
            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehaviour.nestedScrollConnection),
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.settings),
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { onNavigateBack() }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.back)
                                )
                            }
                        },
                        scrollBehavior = scrollBehaviour
                    )
                }
            ) { innerPadding ->
                SettingsVerticalScreen(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp),
                    themePreference = themePreference,
                    switchStates = switchStates,
                    onSelectThemeClick = { onSelectThemeClick() },
                    onLogOutClick = { onLogOutClick() },
                    onSwitchClick = { onSwitchClick(it) },
                    onDeleteAccountClick = { onDeleteAccountClick(it) },
                    onBuyMeACoffeeClick = { onBuyMeACoffeeClick(it) },
                    onBugReportClick = { onBugReportClick(it) },
                    onSuggestFeatureClick = { onSuggestFeatureClick() },
                    onShowLibrariesClick = { onShowLibrariesClick() },
                    onGithubProjectClick = { onGithubProjectClick(it) }
                )
            }
        }

        WindowWidthSizeClass.EXPANDED, WindowWidthSizeClass.MEDIUM -> {
            Box {
                SettingsHorizontalScreen(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    tiles = sectionTiles,
                    selectedTile = selectedTile,
                    onTileSelected = { onTileSelected(it) },
                    themePreference = themePreference,
                    switchStates = switchStates,
                    onSelectThemeClick = { onSelectThemeClick() },
                    onLogOutClick = { onLogOutClick() },
                    onSwitchClick = { onSwitchClick(it) },
                    onDeleteAccountClick = { onDeleteAccountClick(it) },
                    onBuyMeACoffeeClick = { onBuyMeACoffeeClick(it) },
                    onBugReportClick = { onBugReportClick(it) },
                    onSuggestFeatureClick = { onSuggestFeatureClick() },
                    onShowLibrariesClick = { onShowLibrariesClick() },
                    onGithubProjectClick = { onGithubProjectClick(it) }
                )
            }
        }
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { hideThemeDialog() },
            title = {
                Text(
                    text = stringResource(R.string.select_theme),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = themePreference == 0,
                            onClick = { setThemePreference(0) }
                        )
                        Text(
                            text = stringResource(R.string.theme_system_default_auto),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = themePreference == 1,
                            onClick = { setThemePreference(1) }
                        )
                        Text(
                            text = stringResource(R.string.light_theme),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = themePreference == 2,
                            onClick = { setThemePreference(2) }
                        )
                        Text(
                            text = stringResource(R.string.dark_theme),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { hideThemeDialog() }
                ) { (Text(stringResource(R.string.ok))) }
            }
        )
    }


    if (showLogOutDialog) {
        AlertDialog(
            onDismissRequest = { hideLogOutDialog() },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.log_out_confirmation),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.log_out_supporting_text),
                    textAlign = TextAlign.Center
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { hideLogOutDialog() }
                ) { (Text(stringResource(R.string.cancel))) }
            },
            confirmButton = {
                Button(
                    onClick = { onLogoutDialogClick() }
                ) { (Text(stringResource(R.string.yes))) }
            }
        )
    }
}

