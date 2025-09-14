package com.mno.jamscope.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.mno.jamscope.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val LexendGiga = GoogleFont("Lexend Giga")

val LexendGigaFamily = FontFamily(
    Font(googleFont = LexendGiga, fontProvider = provider, weight = FontWeight.Normal), //Normal = Weight 400
    Font(googleFont = LexendGiga, fontProvider = provider, weight = FontWeight.W300),
)

val LoginTypography = Typography(
    headlineSmall = TextStyle(
        fontFamily = LexendGigaFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = LexendGigaFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodySmall = TextStyle(
        fontFamily = LexendGigaFamily,
        fontWeight = FontWeight.W300,
        fontSize = 12.sp
    )
)

// Set of Material typography styles to start with
val Typography = Typography()

val AppTypography = Typography()