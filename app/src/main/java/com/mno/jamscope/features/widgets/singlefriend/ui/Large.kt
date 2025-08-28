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
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.TextStyle
import com.mno.jamscope.features.widgets.WidgetDataStoreManager
import com.mno.jamscope.features.widgets.singlefriend.loadBitmap
import com.mno.jamscope.util.Stuff

@Composable
fun LargeWidgetDesign(context: Context) {
    val friend = WidgetDataStoreManager.getFriend(currentState())
    val textStyle = TextStyle(
        color = GlanceTheme.colors.onSurface,
        fontSize = 17.sp
    )
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(friend) {
        imageBitmap = friend?.image?.firstOrNull { it.size == "large" }?.url?.let {
            loadBitmap(
                it, context
            )
        }
    }
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .padding(2.dp)
            .cornerRadius(Stuff.WIDGET_CORNER_RADIUS),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileWidgetImage(friend, imageBitmap)
        FriendWidgetInfo(
            friend,
            textStyle,
            context,
            forSmall = false
        )
    }
}
