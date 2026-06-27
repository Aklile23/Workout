package com.mypec.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.mypec.app.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val displayGoogleFont = GoogleFont("Space Grotesk")
private val bodyGoogleFont = GoogleFont("Inter")

// Distinctive display family for headings, body family for everything else.
// If Google Play Services can't serve the fonts, Compose falls back gracefully.
val DisplayFamily = FontFamily(
    Font(googleFont = displayGoogleFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = displayGoogleFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = displayGoogleFont, fontProvider = provider, weight = FontWeight.Bold),
)

val BodyFamily = FontFamily(
    Font(googleFont = bodyGoogleFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = bodyGoogleFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = bodyGoogleFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = bodyGoogleFont, fontProvider = provider, weight = FontWeight.Bold),
)

private val default = Typography()

val MyPecTypography = Typography(
    displayLarge = default.displayLarge.copy(fontFamily = DisplayFamily, fontWeight = FontWeight.Bold),
    displayMedium = default.displayMedium.copy(fontFamily = DisplayFamily, fontWeight = FontWeight.Bold),
    displaySmall = default.displaySmall.copy(fontFamily = DisplayFamily, fontWeight = FontWeight.Bold),
    headlineLarge = default.headlineLarge.copy(fontFamily = DisplayFamily, fontWeight = FontWeight.Bold),
    headlineMedium = default.headlineMedium.copy(fontFamily = DisplayFamily, fontWeight = FontWeight.Bold),
    headlineSmall = default.headlineSmall.copy(fontFamily = DisplayFamily, fontWeight = FontWeight.SemiBold),
    titleLarge = default.titleLarge.copy(fontFamily = DisplayFamily, fontWeight = FontWeight.Bold),
    titleMedium = default.titleMedium.copy(fontFamily = BodyFamily, fontWeight = FontWeight.SemiBold),
    titleSmall = default.titleSmall.copy(fontFamily = BodyFamily, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontFamily = BodyFamily, fontSize = 16.sp, fontWeight = FontWeight.Normal, lineHeight = 24.sp),
    bodyMedium = default.bodyMedium.copy(fontFamily = BodyFamily),
    bodySmall = default.bodySmall.copy(fontFamily = BodyFamily),
    labelLarge = default.labelLarge.copy(fontFamily = BodyFamily, fontWeight = FontWeight.SemiBold),
    labelMedium = default.labelMedium.copy(fontFamily = BodyFamily, fontWeight = FontWeight.Medium),
    labelSmall = default.labelSmall.copy(fontFamily = BodyFamily, fontWeight = FontWeight.Medium),
)
