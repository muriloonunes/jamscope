package com.mno.jamscope.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.absoluteValue

object ThemeAttributes {
    private val tertiaryContainerColors = mapOf(
        AppTheme.LIGHT to listOf(
            Color(0xFFFFDCC2),
            Color(0xFFFFDDB8),
            Color(0xFFFFDEAB),
            Color(0xFFFFDF93),
            Color(0xFFF5E393),
            Color(0xFFE9E698),
            Color(0xFFDFE99E),
            Color(0xFFD2ECA9),
            Color(0xFFCBEDAF),
            Color(0xFFC6EEB5),
            Color(0xFFBEEFBE),
            Color(0xFFB4F0CB),
            Color(0xFFB0F0D2),
            Color(0xFFACF0DA),
            Color(0xFFAAF0E1),
            Color(0xFFA8F0E9),
            Color(0xFFA8EFF4),
            Color(0xFFB6EBFF),
            Color(0xFFC1E8FF),
            Color(0xFFC9E6FF),
            Color(0xFFD0E4FF),
            Color(0xFFDCE1FF),
            Color(0xFFE6DEFF),
            Color(0xFFEFDBFF),
            Color(0xFFF8D8FF),
            Color(0xFFFFD6FC),
            Color(0xFFFFD7F1),
            Color(0xFFFFD8EA),
            Color(0xFFFFD9E3),
            Color(0xFFFFDADB),
            Color(0xFFFFDBCF),
        ),
        AppTheme.DARK to listOf(
            Color(0xFF683C10),
            Color(0xFF643F07),
            Color(0xFF5F4102),
            Color(0xFF594401),
            Color(0xFF524704),
            Color(0xFF4A490A),
            Color(0xFF434B11),
            Color(0xFF394D1B),
            Color(0xFF334E20),
            Color(0xFF2E4F24),
            Color(0xFF254F2B),
            Color(0xFF175035),
            Color(0xFF0C513B),
            Color(0xFF005141),
            Color(0xFF005046),
            Color(0xFF00504C),
            Color(0xFF004F53),
            Color(0xFF004E60),
            Color(0xFF044D66),
            Color(0xFF154B6A),
            Color(0xFF22496E),
            Color(0xFF384472),
            Color(0xFF494070),
            Color(0xFF533D6C),
            Color(0xFF5A3B66),
            Color(0xFF5F3961),
            Color(0xFF64385A),
            Color(0xFF683753),
            Color(0xFF6B3649),
            Color(0xFF6E353B),
            Color(0xFF6E3824),

        )
    )

    fun getSecondaryContainerColor(name: String?, appTheme: AppTheme): Color {
        val colors = tertiaryContainerColors[appTheme] ?: return Color.Unspecified
        val index = name.hashCode().absoluteValue % colors.size
        return colors[index]
    }
}