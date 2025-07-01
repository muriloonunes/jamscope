package com.mno.jamscope.ui.screen

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
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
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.mno.jamscope.ui.components.SettingSectionTitle
import com.mno.jamscope.ui.components.SettingsHorizontalMenu
import com.mno.jamscope.ui.components.aboutSettingsSection
import com.mno.jamscope.ui.components.accountSettingsSection
import com.mno.jamscope.ui.components.personalizationSettingsSection
import com.mno.jamscope.ui.theme.LocalWindowSizeClass
import com.mno.jamscope.ui.viewmodel.SettingsViewModel
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

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

@Composable
fun SettingsHorizontalScreen(
    modifier: Modifier = Modifier,
    themePreference: Int,
    switchStates: Map<String, Boolean>,
    settingsViewModel: SettingsViewModel,
    onSelectThemeClick: () -> Unit,
    onLogOutClick: () -> Unit,
    tiles: List<Int>,
    context: Context
) {
    var selectedTile by remember { mutableIntStateOf(tiles.first()) }
    Row(
        modifier = modifier
    ) {
        SettingsHorizontalMenu(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = 8.dp),
            tiles = tiles,
            selected = selectedTile,
            onTileSelected = { selectedTile = it }
        )
        VerticalDivider()
        AnimatedContent(
            targetState = selectedTile,
            transitionSpec = {
                val currentIndex = tiles.indexOf(targetState)
                val previousIndex = tiles.indexOf(initialState)

                val duracao = 250

                if (currentIndex > previousIndex) {
                    slideInVertically(
                        animationSpec = tween(durationMillis = duracao)
                    ) { height -> height } + fadeIn() togetherWith
                            slideOutVertically(
                                animationSpec = tween(durationMillis = duracao)
                            ) { height -> -height } + fadeOut()
                } else {
                    slideInVertically(
                        animationSpec = tween(durationMillis = duracao)
                    ) { height -> -height } + fadeIn() togetherWith
                            slideOutVertically(
                                animationSpec = tween(durationMillis = duracao)
                            ) { height -> height } + fadeOut()
                }.using(
                    SizeTransform(clip = false)
                )
            },
            modifier = Modifier
                .weight(2f)
        ) { tile ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                when (tile) {
                    R.string.personalization_setting_title -> {
                        item {
                            SettingSectionTitle(R.string.personalization_setting_title)
                        }
                        personalizationSettingsSection(
                            themePreference = themePreference,
                            onSelectThemeClick = onSelectThemeClick,
                            switchStates = switchStates,
                            onSwitchClick = { settingsViewModel.toggleSwitch(it) }
                        )
                    }

                    R.string.account_setting_tile -> {
                        item {
                            SettingSectionTitle(R.string.account_setting_tile)
                        }
                        accountSettingsSection(
                            onDeleteAccountClick = { settingsViewModel.openDeleteAccount(context) },
                            onLogOutClick = onLogOutClick,
                        )
                    }

                    R.string.about_setting_tile -> {
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
        }
    }
}