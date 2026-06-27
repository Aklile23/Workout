package com.mypec.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val default = Typography()

val MyPecTypography = Typography(
    displayLarge = default.displayLarge.copy(fontWeight = FontWeight.Bold),
    displayMedium = default.displayMedium.copy(fontWeight = FontWeight.Bold),
    headlineLarge = default.headlineLarge.copy(fontWeight = FontWeight.Bold),
    headlineMedium = default.headlineMedium.copy(fontWeight = FontWeight.Bold),
    headlineSmall = default.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
    titleLarge = default.titleLarge.copy(fontWeight = FontWeight.Bold),
    titleMedium = default.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    labelLarge = default.labelLarge.copy(fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, lineHeight = 24.sp),
)
