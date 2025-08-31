package com.mno.jamscope.features.widgets.singlefriend.ui

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.TextStyle
import com.mno.jamscope.features.widgets.WidgetDataStoreManager
import com.mno.jamscope.features.widgets.singlefriend.clickableWidget
import com.mno.jamscope.features.widgets.singlefriend.loadBitmap
import com.mno.jamscope.util.Stuff

@Composable
fun SmallWidgetDesign(
    context: Context,
) {
    val friend = WidgetDataStoreManager.getFriend(currentState())
    val textStyle = TextStyle(
        color = GlanceTheme.colors.onSurface,
    )
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(friend) {
        imageBitmap = friend?.image?.firstOrNull { it.size == "large" }?.url?.let {
            loadBitmap(
                it, context
            )
        }
    }
    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .padding(2.dp)
            .cornerRadius(Stuff.WIDGET_CORNER_RADIUS)
            .clickableWidget(friend?.name),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileWidgetImage(friend, imageBitmap)
        FriendWidgetInfo(
            friend =  friend,
            textStyle = textStyle,
            context = context,
            forSmall = true
        )
    }
}