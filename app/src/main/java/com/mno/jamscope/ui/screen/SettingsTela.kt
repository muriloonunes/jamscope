package com.mno.jamscope.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.BrightnessMedium
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.mno.jamscope.R
import com.mno.jamscope.ui.components.SettingSectionTitle
import com.mno.jamscope.ui.components.SettingSwitch
import com.mno.jamscope.ui.components.SettingsClickableComp
import com.mno.jamscope.ui.viewmodel.SettingsViewModel
import com.mno.jamscope.util.switches
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTela(
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior()
    val switches = switches
    val switchStates by settingsViewModel.switchStates.collectAsState()
    val themePreference by settingsViewModel.themePreference.collectAsState()

    var showLogOutDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    Scaffold(
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
        LazyColumnScrollbar(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            state = rememberLazyListState(),
            settings = ScrollbarSettings(
                thumbUnselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                thumbSelectedColor = MaterialTheme.colorScheme.onSurface,
                scrollbarPadding = 2.dp
            ),
        ) {
            LazyColumn {
                item {
                    SettingSectionTitle(R.string.personalization_setting_title)
                }
                item {
                    SettingsClickableComp(
                        icon = Icons.Outlined.BrightnessMedium,
                        iconDesc = R.string.theme_icon,
                        name = R.string.app_theme_setting,
                        subtitle = when (themePreference) {
                            0 -> R.string.theme_system_default_auto
                            1 -> R.string.light_theme
                            2 -> R.string.dark_theme
                            else -> R.string.theme_system_default_auto
                        },
                        showIcon = false
                    ) {
                        showThemeDialog = true
                    }
                }
                items(switches) { switch ->
                    val state = switchStates[switch.key] ?: switch.initialState
                    SettingSwitch(
                        icon = switch.icon,
                        iconDesc = switch.iconDesc,
                        name = switch.name,
                        state = state,
                        onClick = {
                            settingsViewModel.toggleSwitch(switch.key)
                        }
                    )
                }
                item {
                    SettingSectionTitle(R.string.account_setting_tile)
                }
                item {
                    SettingsClickableComp(
                        icon = Icons.Outlined.Delete,
                        iconDesc = R.string.theme_icon,
                        name = R.string.delete_account_setting,
                        subtitle = R.string.last_fm_delete_account,
                        showIcon = false
                    ) {
                        settingsViewModel.openDeleteAccount(context)
                    }
                }
                item {
                    SettingsClickableComp(
                        icon = Icons.AutoMirrored.Outlined.Logout,
                        iconDesc = R.string.log_out_icon,
                        name = R.string.logout,
                        showIcon = false,
                        subtitle = null
                    ) {
                        showLogOutDialog = true
                    }
                }
                item {
                    SettingSectionTitle(R.string.about_setting_tile)
                }
                item {
                    SettingsClickableComp(
                        icon = Icons.Outlined.Coffee,
                        iconDesc = R.string.coffee_icon,
                        name = R.string.support_this_app_setting,
                        subtitle = R.string.buy_me_a_coffee,
                        showIcon = false
                    ) {
                        settingsViewModel.openBuyMeACoffee(context)
                    }
                }
                item {
                    SettingsClickableComp(
                        icon = Icons.Outlined.BugReport,
                        iconDesc = R.string.bug_report_icon,
                        name = R.string.report_bug_setting,
                        subtitle = null,
                        showIcon = true
                    ) {
                        settingsViewModel.sendBugReportMail(context)
                    }
                }
                item {
                    SettingsClickableComp(
                        icon = Icons.Outlined.ModeComment,
                        iconDesc = R.string.suggest_feature_icon,
                        name = R.string.suggest_feature_setting,
                        subtitle = null,
                        showIcon = true
                    ) {
                        settingsViewModel.navigateToWebView()
                    }
                }
                item {
                    SettingsClickableComp(
                        icon = Icons.AutoMirrored.Outlined.LibraryBooks,
                        iconDesc = R.string.libraries_icon,
                        name = R.string.show_libraries_setting,
                        subtitle = null,
                        showIcon = true
                    ) {
                        settingsViewModel.navigateToLibraries()
                    }
                }
                item {
                    SettingsClickableComp(
                        icon = Icons.Outlined.Info,
                        iconDesc = R.string.info_icon,
                        name = R.string.app_name,
                        subtitle = R.string.github_project_link,
                        showIcon = false
                    ) {
                        settingsViewModel.openGithubProject(context)
                    }
                }
//                item {
//                    SettingsClickableComp(
//                        icon = Icons.Outlined.Star,
//                        iconDesc = R.string.star_icon,
//                        name = R.string.liking_app_text,
//                        subtitle = R.string.liking_app_expanded_text,
//                        showIcon = false
//                    ) {
//                        settingsViewModel.openPlayStore(context)
//                    }
//                }
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