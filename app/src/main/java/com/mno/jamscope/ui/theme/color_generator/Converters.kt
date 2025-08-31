package com.mno.jamscope.ui.theme.color_generator

import kotlin.math.abs
import kotlin.math.roundToInt

fun String.toArgb(): Int {
    if (this[0] == '#') {
        var color = this.substring(1).toLong(16)
        if (this.length == 7) {
            color = color or 0x00000000ff000000L
        } else require(this.length == 9) { "Unknown color" }
        return color.toInt()
    } else {
        throw IllegalArgumentException("Unknown color")
    }
}

fun Int.toHexColor(includeAlpha: Boolean = false): String {
    val a = this.shr(24) and 0xFF
    val r = this.shr(16) and 0xFF
    val g = this.shr(8) and 0xFF
    val b = this and 0xFF

    return if (includeAlpha) {
        String.format("#%02X%02X%02X%02X", a, r, g, b)
    } else {
        String.format("#%02X%02X%02X", r, g, b)
    }
}

fun String.toColorLong(): String {
    if (this[0] == '#') {
        val newString = this.removePrefix("#")
        return "0xFF$newString"
    } else {
        throw IllegalArgumentException("Unknown color")
    }
}

fun hsvToArgb(h: Float, s: Float, v: Float): Int {
    val c = v * s
    val x = c * (1 - abs((h / 60f) % 2 - 1))
    val m = v - c

    val (r1, g1, b1) = when {
        h < 60 -> Triple(c, x, 0f)
        h < 120 -> Triple(x, c, 0f)
        h < 180 -> Triple(0f, c, x)
        h < 240 -> Triple(0f, x, c)
        h < 300 -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }

    val r = ((r1 + m) * 255).roundToInt()
    val g = ((g1 + m) * 255).roundToInt()
    val b = ((b1 + m) * 255).roundToInt()

    return (0xFF shl 24) or (r shl 16) or (g shl 8) or b
}