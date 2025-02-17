package com.mno.jamscope.util

import java.util.Locale
//thanks pano-scrobbler
val countryCodesMap by lazy {
    val countries = hashMapOf<String, String>()
    Locale.getISOCountries().forEach { iso ->
        val l = Locale("en", iso)
        countries[l.getDisplayCountry(l)] = iso
    }
    countries
}

fun getCountryFlag(countryName: String): String {
    val isoCode = countryCodesMap[countryName] ?: return ""
    val flagEmoji = StringBuilder()
    isoCode.forEach {
        val codePoint = 127397 + it.code
        flagEmoji.appendCodePoint(codePoint)
    }
    return flagEmoji.toString()
}