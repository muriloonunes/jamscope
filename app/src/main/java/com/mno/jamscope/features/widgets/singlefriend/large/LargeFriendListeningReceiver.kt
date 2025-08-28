package com.mno.jamscope.features.widgets.singlefriend.large

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.mno.jamscope.features.widgets.singlefriend.FriendListeningWidget

class LargeFriendListeningReceiver: GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = FriendListeningWidget()
}