package com.mypec.app.ui.theme

import androidx.compose.ui.graphics.Color

// ---- Single accent (electric lime) ----
val Lime = Color(0xFFCBFF4D)
val LimeDim = Color(0xFFB2EA34)
val LimeDeep = Color(0xFF55700E) // readable lime on light backgrounds
val OnAccent = Color(0xFF12140A) // near-black text/icons that sit on lime

// ---- Functional ----
val Coral = Color(0xFFFF6B6B)

// ---- Dark neutrals (charcoal) ----
val DarkBg = Color(0xFF0A0A0C)
val DarkSurface = Color(0xFF141416)
val DarkSurfaceVariant = Color(0xFF222227)
val DarkOnBg = Color(0xFFF4F4F5)
val DarkOnBgMuted = Color(0xFF97969E)
val DarkOnPrimary = OnAccent

// ---- Light neutrals ----
val LightBg = Color(0xFFF6F6F4)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFECECEA)
val LightOnBg = Color(0xFF131316)
val LightOnBgMuted = Color(0xFF6B6B72)

// ---- Gradients (kept subtle; lime is the only accent) ----
val PrimaryGradient = listOf(Color(0xFFD4FF5C), Color(0xFFB2EA34))
val AccentGradient = PrimaryGradient
val DarkCardGradient = listOf(Color(0xFF18181B), Color(0xFF111113))

// ---- Faint background glow ----
val GlowAccent = Lime
