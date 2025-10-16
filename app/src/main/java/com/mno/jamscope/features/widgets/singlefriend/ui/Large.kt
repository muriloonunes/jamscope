package com.mno.jamscope.features.widgets.singlefriend.ui

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.text.TextStyle
import com.mno.jamscope.domain.model.Friend

@Composable
fun LargeWidgetDesign(
    modifier: GlanceModifier,
    context: Context,
    friend: Friend?,
    textStyle: TextStyle,
    imageBitmap: Bitmap?,
) {
    Column(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Top,
        ) {
            ProfileWidgetImage(
                friend,
                imageBitmap,
                size = 124.dp,
                cornerRadius = 24.dp
            )
            FriendWidgetInfo(
                friend,
                textStyle,
                context,
                forSmall = false
            )
        }
        Spacer(GlanceModifier.height(6.dp))
        LastUpdatedManager(false)
    }
}
