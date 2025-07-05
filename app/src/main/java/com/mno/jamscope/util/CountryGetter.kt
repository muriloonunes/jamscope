package com.mno.jamscope.util

import java.util.Locale

//thanks pano-scrobbler
val countryCodesMap by lazy {
    val countries = hashMapOf<String, String>()
    Locale.getISOCountries().forEach { iso ->
        val l = Locale.forLanguageTag("en-$iso")
        countries[l.getDisplayCountry(Locale.ENGLISH)] = iso
    }
    countries
}

//thanks pano-scrobbler
fun getCountryFlag(countryName: String): String {
    val isoCode = countryCodesMap[countryName] ?: return ""
    val flagEmoji = StringBuilder()
    isoCode.forEach {
        val codePoint = 127397 + it.code
        flagEmoji.appendCodePoint(codePoint)
    }
    return flagEmoji.toString()
}

fun getLocalizedCountryName(countryName: String): String {
    val isoCode = countryCodesMap[countryName]
    return isoCode?.let {
        Locale.Builder().setRegion(it).build().getDisplayCountry(Locale.getDefault())
    } ?: countryName
}