package com.mno.jamscope.features.settings.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.BrightnessMedium
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mno.jamscope.R
import com.mno.jamscope.features.settings.domain.model.getPersonalizationSwitches

@Composable
private fun SettingSection(
    @StringRes name: Int,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(id = name),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.surfaceTint,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
        )
        Column(modifier = Modifier.clip(shape = RoundedCornerShape(24.dp))) {
            content()
        }
    }
}
fun LazyListScope.personalizationSettingsSection(
    themePreference: Int,
    onSelectThemeClick: () -> Unit,
    switchStates: Map<String, Boolean>,
    onSwitchClick: (String) -> Unit
) {
    item {
        val personalizationSwitches = getPersonalizationSwitches()
        SettingSection(R.string.personalization_setting_title) {
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
                onSelectThemeClick()
            }

            personalizationSwitches.forEach { switch ->
                val state = switchStates[switch.key] ?: switch.initialState
                SettingSwitch(
                    icon = switch.icon,
                    iconDesc = switch.iconDesc,
                    name = switch.name,
                    state = state,
                    onClick = {
                        onSwitchClick(switch.key)
                    }
                )
            }
        }
    }
}

fun LazyListScope.accountSettingsSection(
    onDeleteAccountClick: () -> Unit,
    onLogOutClick: () -> Unit
) {
    item {
        SettingSection(R.string.account_setting_tile) {
            SettingsClickableComp(
                icon = Icons.Outlined.Delete,
                iconDesc = R.string.theme_icon,
                name = R.string.delete_account_setting,
                subtitle = R.string.last_fm_delete_account,
                showIcon = false
            ) {
                onDeleteAccountClick()
            }
            Spacer(Modifier.height(4.dp))
            SettingsClickableComp(
                icon = Icons.AutoMirrored.Outlined.Logout,
                iconDesc = R.string.log_out_icon,
                name = R.string.logout,
                showIcon = false,
                subtitle = null
            ) {
                onLogOutClick()
            }
        }
    }
}

fun LazyListScope.aboutSettingsSection(
    onBuyMeACoffeeClick: () -> Unit,
    onBugReportClick: () -> Unit,
    onSuggestFeatureClick: () -> Unit,
    onShowLibrariesClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    item {
        SettingSection(R.string.about_setting_tile) {
            SettingsClickableComp(
                icon = Icons.Outlined.Coffee,
                iconDesc = R.string.coffee_icon,
                name = R.string.support_this_app_setting,
                subtitle = R.string.buy_me_a_coffee,
                showIcon = false
            ) {
                onBuyMeACoffeeClick()
            }
            Spacer(Modifier.height(4.dp))
            SettingsClickableComp(
                icon = Icons.Outlined.ReportProblem,
                iconDesc = R.string.bug_report_icon,
                name = R.string.report_bug_setting,
                subtitle = null,
                showIcon = true
            ) {
                onBugReportClick()
            }
            Spacer(Modifier.height(4.dp))
            SettingsClickableComp(
                icon = Icons.Outlined.ModeComment,
                iconDesc = R.string.suggest_feature_icon,
                name = R.string.suggest_feature_setting,
                subtitle = null,
                showIcon = true
            ) {
                onSuggestFeatureClick()
            }
            Spacer(Modifier.height(4.dp))
            SettingsClickableComp(
                icon = Icons.AutoMirrored.Outlined.LibraryBooks,
                iconDesc = R.string.libraries_icon,
                name = R.string.show_libraries_setting,
                subtitle = null,
                showIcon = true
            ) {
                onShowLibrariesClick()
            }
            Spacer(Modifier.height(4.dp))
            SettingsClickableComp(
                icon = Icons.Outlined.Info,
                iconDesc = R.string.info_icon,
                name = R.string.app_name,
                subtitle = null,
                showIcon = true
            ) {
                onAboutClick()
            }
        }
    }
//    item {
//        SettingsClickableComp(
//            icon = Icons.Outlined.Star,
//            iconDesc = R.string.star_icon,
//            name = R.string.liking_app_text,
//            subtitle = R.string.liking_app_expanded_text,
//            showIcon = false
//        ) {
//            settingsViewModel.openPlayStore(context)
//        }
//    }
}