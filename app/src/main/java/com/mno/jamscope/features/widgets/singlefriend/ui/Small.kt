package com.mno.jamscope.features.widgets.singlefriend.ui

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.text.TextStyle
import com.mno.jamscope.domain.model.Friend

@Composable
fun SmallWidgetDesign(
    modifier: GlanceModifier,
    context: Context,
    friend: Friend?,
    textStyle: TextStyle,
    imageBitmap: Bitmap?,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileWidgetImage(friend, imageBitmap)
        FriendWidgetInfo(
            friend = friend,
            textStyle = textStyle,
            context = context,
            forSmall = true
        )
    }
}