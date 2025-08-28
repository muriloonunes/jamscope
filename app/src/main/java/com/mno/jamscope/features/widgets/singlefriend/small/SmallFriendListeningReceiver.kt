package com.mno.jamscope.features.widgets.singlefriend.small

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.mno.jamscope.features.widgets.singlefriend.FriendListeningWidget

class SmallFriendListeningReceiver: GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = FriendListeningWidget()
}