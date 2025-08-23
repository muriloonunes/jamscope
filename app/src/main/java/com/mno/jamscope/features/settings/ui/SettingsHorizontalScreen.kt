package com.mno.jamscope.features.settings.ui

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mno.jamscope.R
import com.mno.jamscope.features.settings.viewmodel.SettingsViewModel
import com.mno.jamscope.ui.components.SettingSectionTitle
import com.mno.jamscope.ui.components.SettingsHorizontalMenu
import com.mno.jamscope.ui.components.aboutSettingsSection
import com.mno.jamscope.ui.components.accountSettingsSection
import com.mno.jamscope.ui.components.personalizationSettingsSection

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