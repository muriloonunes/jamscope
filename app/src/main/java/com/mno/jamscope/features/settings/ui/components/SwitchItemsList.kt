package com.mno.jamscope.features.settings.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PlayCircleOutline
import com.mno.jamscope.R
import com.mno.jamscope.util.Stuff

val switches = listOf(
    Stuff.SwitchItem(
        key = "card_background_color_toggle",
        name = R.string.card_colored_background_setting,
        icon = Icons.Outlined.Palette,
        iconDesc = R.string.palette_icon,
        initialState = true //ON
    ),
    Stuff.SwitchItem(
        key = "playing_animation_toggle",
        name = R.string.playing_animation_setting,
        icon = Icons.Outlined.PlayCircleOutline,
        iconDesc = R.string.animation_play_icon,
        initialState = true //ON
    ),
//    Stuff.SwitchItem(
//        key = "background_updates_toggle",
//        name = R.string.background_update_setting,
//        icon = Icons.Outlined.Update,
//        iconDesc = R.string.update_icon,
//        initialState = false //OFF
//    )
)