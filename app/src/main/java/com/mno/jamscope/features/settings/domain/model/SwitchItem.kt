package com.mno.jamscope.features.settings.domain.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.ui.graphics.vector.ImageVector
import com.mno.jamscope.R

data class SwitchItem(
    val key: String,
    @param:StringRes val name: Int,
    val icon: ImageVector,
    @param:StringRes val iconDesc: Int,
    val initialState: Boolean,
)

fun getPersonalizationSwitches(): List<SwitchItem> {
    return listOf(
        SwitchItem(
            key = "card_background_color_toggle",
            name = R.string.card_colored_background_setting,
            icon = Icons.Outlined.Palette,
            iconDesc = R.string.palette_icon,
            initialState = true //ON
        ),
        SwitchItem(
            key = "playing_animation_toggle",
            name = R.string.playing_animation_setting,
            icon = Icons.Outlined.PlayCircleOutline,
            iconDesc = R.string.animation_play_icon,
            initialState = true //ON
        ),
//            SwitchItem(
//                key = "background_updates_toggle",
//                name = R.string.background_update_setting,
//                icon = Icons.Outlined.Update,
//                iconDesc = R.string.update_icon,
//                initialState = false //OFF
//            )
    )
}
