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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mno.jamscope.R
import com.mno.jamscope.features.settings.ui.components.SettingSectionTitle
import com.mno.jamscope.features.settings.ui.components.SettingsHorizontalMenu
import com.mno.jamscope.features.settings.ui.components.aboutSettingsSection
import com.mno.jamscope.features.settings.ui.components.accountSettingsSection
import com.mno.jamscope.features.settings.ui.components.personalizationSettingsSection

@Composable
fun SettingsHorizontalScreen(
    modifier: Modifier = Modifier,
    themePreference: Int,
    switchStates: Map<String, Boolean>,
    onSelectThemeClick: () -> Unit,
    onLogOutClick: () -> Unit,
    tiles: List<Int>,
    selectedTile: Int,
    onTileSelected: (Int) -> Unit,
    onSwitchClick: (String) -> Unit,
    onDeleteAccountClick: (Context) -> Unit,
    onBuyMeACoffeeClick: (Context) -> Unit,
    onBugReportClick: (Context) -> Unit,
    onSuggestFeatureClick: () -> Unit,
    onShowLibrariesClick: () -> Unit,
    onGithubProjectClick: (Context) -> Unit,
) {
    val context = LocalContext.current
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
            onTileSelected = { onTileSelected(it) }
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
                            onSwitchClick = { onSwitchClick(it) }
                        )
                    }

                    R.string.account_setting_tile -> {
                        item {
                            SettingSectionTitle(R.string.account_setting_tile)
                        }
                        accountSettingsSection(
                            onDeleteAccountClick = { onDeleteAccountClick(context) },
                            onLogOutClick = onLogOutClick,
                        )
                    }

                    R.string.about_setting_tile -> {
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
        }
    }
}