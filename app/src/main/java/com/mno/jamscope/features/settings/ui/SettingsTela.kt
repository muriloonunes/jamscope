package com.mno.jamscope.features.settings.ui

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.mno.jamscope.R
import com.mno.jamscope.features.settings.viewmodel.SettingsViewModel
import com.mno.jamscope.ui.theme.LocalWindowSizeClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTela(
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior()
    val switchStates by settingsViewModel.switchStates.collectAsState()
    val themePreference by settingsViewModel.themePreference.collectAsState()
    val windowSizeClass = LocalWindowSizeClass.current

    var showLogOutDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    val windowWidth = windowSizeClass.windowWidthSizeClass

    val sectionTiles = listOf(
        R.string.personalization_setting_title,
        R.string.account_setting_tile,
        R.string.about_setting_tile
    )
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
                        onClick = {
                            settingsViewModel.navigateBack()
                        }
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
        when (windowWidth) {
            WindowWidthSizeClass.COMPACT -> {
                SettingsVerticalScreen(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp),
                    themePreference = themePreference,
                    switchStates = switchStates,
                    settingsViewModel = settingsViewModel,
                    context = context,
                    onSelectThemeClick = { showThemeDialog = true },
                    onLogOutClick = { showLogOutDialog = true }
                )
            }

            WindowWidthSizeClass.EXPANDED, WindowWidthSizeClass.MEDIUM -> {
                SettingsHorizontalScreen(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    tiles = sectionTiles,
                    themePreference = themePreference,
                    switchStates = switchStates,
                    settingsViewModel = settingsViewModel,
                    onSelectThemeClick = { showThemeDialog = true },
                    onLogOutClick = { showLogOutDialog = true },
                    context = context
                )
            }
        }
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
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
                            onClick = { settingsViewModel.setThemePreference(0) }
                        )
                        Text(
                            text = stringResource(R.string.theme_system_default_auto),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = themePreference == 1,
                            onClick = { settingsViewModel.setThemePreference(1) }
                        )
                        Text(
                            text = stringResource(R.string.light_theme),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = themePreference == 2,
                            onClick = { settingsViewModel.setThemePreference(2) }
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
                    onClick = { showThemeDialog = false }
                ) { (Text(stringResource(R.string.ok))) }
            }
        )
    }


    if (showLogOutDialog) {
        AlertDialog(
            onDismissRequest = { showLogOutDialog = false },
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
                    onClick = { showLogOutDialog = false }
                ) { (Text(stringResource(R.string.cancel))) }
            },
            confirmButton = {
                Button(
                    onClick = {
                        settingsViewModel.logOutUser()
                        showLogOutDialog = false
                    }
                ) { (Text(stringResource(R.string.yes))) }
            }
        )
    }
}

