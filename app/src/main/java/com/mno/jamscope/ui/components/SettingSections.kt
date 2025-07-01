package com.mno.jamscope.ui.components

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.BrightnessMedium
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.ModeComment
import com.mno.jamscope.R
import com.mno.jamscope.util.switches

fun LazyListScope.personalizationSettingsSection(
    themePreference: Int,
    onSelectThemeClick: () -> Unit,
    switchStates: Map<String, Boolean>,
    onSwitchClick: (String) -> Unit
) {
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
            onSelectThemeClick()
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
                onSwitchClick(switch.key)
            }
        )
    }
}

fun LazyListScope.accountSettingsSection(
    onDeleteAccountClick: () -> Unit,
    onLogOutClick: () -> Unit
) {
    item {
        SettingsClickableComp(
            icon = Icons.Outlined.Delete,
            iconDesc = R.string.theme_icon,
            name = R.string.delete_account_setting,
            subtitle = R.string.last_fm_delete_account,
            showIcon = false
        ) {
            onDeleteAccountClick()
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
            onLogOutClick()
        }
    }
}

fun LazyListScope.aboutSettingsSection(
    onBuyMeACoffeeClick: () -> Unit,
    onBugReportClick: () -> Unit,
    onSuggestFeatureClick: () -> Unit,
    onShowLibrariesClick: () -> Unit,
    onGithubProjectClick: () -> Unit
) {
    item {
        SettingsClickableComp(
            icon = Icons.Outlined.Coffee,
            iconDesc = R.string.coffee_icon,
            name = R.string.support_this_app_setting,
            subtitle = R.string.buy_me_a_coffee,
            showIcon = false
        ) {
            onBuyMeACoffeeClick()
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
            onBugReportClick()
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
            onSuggestFeatureClick()
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
            onShowLibrariesClick()
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
            onGithubProjectClick()
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