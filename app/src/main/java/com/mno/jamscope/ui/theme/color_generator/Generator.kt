package com.mno.jamscope.ui.theme.color_generator

import android.annotation.SuppressLint
import com.google.android.material.color.utilities.Hct
import com.google.android.material.color.utilities.SchemeVibrant
import kotlin.math.pow
import kotlin.math.sqrt

//val baseColors = listOf(
//    "#cf6b43",
//    "#7a92a8",
//    "#a5ab3d",
//    "#e7ab6a",
//    "#ff204e",
//    "#d4c3b7",
//    "#e25219",
//    "#5587c2",
//    "#046144",
//    "#fdfdfd",
//    "#17181c",
//    "#ab5d9b",
//    "#e6ca1e",
//    "#899582",
//    "#f9f1c9",
//    "#af152d",
//    "#092f3e",
//    "#5d3f22",
//    "#feb0d2",
//    "#23224e",
//    "#cdb82e",
//    "#777777",
//    "#eb6112",
//    "#d6b371"
//)

fun main() {
    println("Starting Generator!")
    val baseColors = generateHueWheel(5)
    val tertiaryColors = generateTertiaryFromColors(baseColors)
    println(baseColors.size)

    val filtered = mutableListOf<Pair<String, String>>()
    val threshold = 7.0
    for ((light, dark) in tertiaryColors) {
        val argb = light.toArgb()
        if (filtered.none { colorDistance(it.first.toArgb(), argb) < threshold }) {
            filtered.add(light to dark)
        }
    }

//    tertiaryColors.forEachIndexed { index, (light, dark) ->
//        generateOverview(index, light, dark)
//    }

    filtered.forEach { (light, _) ->
        generateForThemeAttributesLight(light)
    }
    println()
    filtered.forEach { (_, dark) ->
        generateForThemeAttributesDark(dark)
    }
}

fun generateHueWheel(step: Int = 15): List<String> {
    val colors = mutableListOf<String>()
    for (hue in 0 until 360 step step) {
        val rgb = hsvToArgb(hue.toFloat(), 0.8f, 0.9f)
        colors.add(rgb.toHexColor())
    }
    return colors
}

@SuppressLint("RestrictedApi")
fun generateTertiaryFromColors(hexColors: List<String>): List<Pair<String, String>> {
    return hexColors.map { hex ->
        try {
            val argb = hex.toArgb()
            val hct = Hct.fromInt(argb)

            val schemeLight = SchemeVibrant(hct, /* isDark = */ false, 0.0)
            val tertiaryContainerLight = schemeLight.tertiaryContainer.toHexColor()

            val schemeDark = SchemeVibrant(hct, /* isDark = */ true, 0.0)
            val tertiaryContainerDark = schemeDark.tertiaryContainer.toHexColor()

            tertiaryContainerLight to tertiaryContainerDark
        } catch (_: IllegalArgumentException) {
            "Unknown" to "Unknown"
        }
    }
}

fun colorDistance(c1: Int, c2: Int): Double {
    val r1 = (c1 shr 16) and 0xFF
    val g1 = (c1 shr 8) and 0xFF
    val b1 = c1 and 0xFF

    val r2 = (c2 shr 16) and 0xFF
    val g2 = (c2 shr 8) and 0xFF
    val b2 = c2 and 0xFF

    return sqrt(
        ((r1 - r2).toDouble().pow(2)) +
                ((g1 - g2).toDouble().pow(2)) +
                ((b1 - b2).toDouble().pow(2))
    )
}

fun generateOverview(baseColors: List<String>, index: Int, light: String, dark: String) {
    println("Base Color: ${baseColors[index]} -> TertiaryContainer Light: $light, TertiaryContainer Dark: $dark")
}

fun generateForThemeAttributesLight(light: String) {
    println("Color(${light.toColorLong()}),")
}

fun generateForThemeAttributesDark(dark: String) {
    println("Color(${dark.toColorLong()}),")
}
