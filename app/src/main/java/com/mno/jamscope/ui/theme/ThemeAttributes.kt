package com.mno.jamscope.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.absoluteValue

object ThemeAttributes {
    private val secondaryContainerColors = mapOf(
        AppTheme.LIGHT to listOf(
            Color(0xFFFFDBD1),
            Color(0xFFDCE7C8),
            Color(0xFFDAE2F9),
            Color(0xFFEEE2BC),
            Color(0xFFFEDDBE),
            Color(0xFFF1DCF4),
            Color(0xFFFFD9E2),
            Color(0xFFFFDCC5),
            Color(0xFFCDE7EC),
            Color(0xFFE5E5C0),
            Color(0xFFD6E8CE),
            Color(0xFFCEE9DB),
            Color(0xFFCCE8E6),
            Color(0xFFDCE2F9),
            Color(0xFFE1E0F9),
            Color(0xFFFFDBCC),
            Color(0xFFF0E2BB),
            Color(0xFFF5DBF0),
            Color(0xFFFFDBCE),
            Color(0xFFD8E7CB),
            Color(0xFFD1E5F4),
            Color(0xFFE9DEF8),
            Color(0xFFFFDCC2),
            Color(0xFFDBE7C8),
            Color(0xFFD3E8D1),
            Color(0xFFFFDBD2)
        ),
        AppTheme.DARK to listOf(
            Color(0xFF5D4037),
            Color(0xFF404A33),
            Color(0xFF3E4759),
            Color(0xFF4E472A),
            Color(0xFF58432C),
            Color(0xFF504255),
            Color(0xFF5A3F47),
            Color(0xFF5B412F),
            Color(0xFF334B4F),
            Color(0xFF47482E),
            Color(0xFF3C4B38),
            Color(0xFF354C42),
            Color(0xFF324B4A),
            Color(0xFF404659),
            Color(0xFF444559),
            Color(0xFF5D4033),
            Color(0xFF4F462A),
            Color(0xFF534152),
            Color(0xFF5D4035),
            Color(0xFF3D4B36),
            Color(0xFF364955),
            Color(0xFF4A4458),
            Color(0xFF5A422E),
            Color(0xFF404A34),
            Color(0xFF394B3A),
            Color(0xFF5D4038)
        )
    )

    fun getSecondaryContainerColor(name: String?, appTheme: AppTheme): Color {
        val colors = secondaryContainerColors[appTheme] ?: return Color.Unspecified
        val index = (name.hashCode() * 28).absoluteValue % colors.size
        return colors[index]
    }
}