package com.example.Finova.core.design.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

// ============================================================
// FINOVA — Tipografía
// Fuente: DESIGN.md (sección Typography)
// Nota: usamos SansSerif (≈ Inter en Android moderno).
//       Si luego meten Inter real en res/font/, solo cambian
//       la FontFamily de abajo y todo el theme se actualiza.
// ============================================================

private val FinovaFontFamily = FontFamily.SansSerif

// ---------- Escala completa (por si se usan directo) ----------
val FinovaDisplayLgMobile = TextStyle(
    fontFamily = FinovaFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 30.sp,
    lineHeight = 38.sp,
    letterSpacing = (-0.02).em
)

val FinovaDisplayLg = TextStyle(
    fontFamily = FinovaFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 36.sp,
    lineHeight = 44.sp,
    letterSpacing = (-0.02).em
)

val FinovaHeadlineLg = TextStyle(
    fontFamily = FinovaFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 24.sp,
    lineHeight = 32.sp,
    letterSpacing = (-0.01).em
)

val FinovaHeadlineMd = TextStyle(
    fontFamily = FinovaFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
    lineHeight = 28.sp,
    letterSpacing = (-0.01).em
)

val FinovaHeadlineSm = TextStyle(
    fontFamily = FinovaFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    lineHeight = 24.sp
)

val FinovaTitleMd = TextStyle(
    fontFamily = FinovaFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 22.sp
)

val FinovaBodyLg = TextStyle(
    fontFamily = FinovaFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp
)

val FinovaBodyMd = TextStyle(
    fontFamily = FinovaFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp
)

val FinovaBodySm = TextStyle(
    fontFamily = FinovaFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 16.sp
)

val FinovaLabelLg = TextStyle(
    fontFamily = FinovaFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.01.em
)

val FinovaLabelMd = TextStyle(
    fontFamily = FinovaFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.02.em
)

val FinovaLabelSm = TextStyle(
    fontFamily = FinovaFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 10.sp,
    lineHeight = 14.sp,
    letterSpacing = 0.04.em
)

// ============================================================
// Mapeo a los slots de Material 3 Typography
// ============================================================
val FinovaTypography = Typography(
    displayLarge   = FinovaDisplayLg,
    displayMedium  = FinovaDisplayLgMobile,
    displaySmall   = FinovaHeadlineLg,

    headlineLarge  = FinovaHeadlineLg,
    headlineMedium = FinovaHeadlineMd,
    headlineSmall  = FinovaHeadlineSm,

    titleLarge     = FinovaTitleMd,
    titleMedium    = FinovaTitleMd,
    titleSmall     = FinovaLabelLg,

    bodyLarge      = FinovaBodyLg,
    bodyMedium     = FinovaBodyMd,
    bodySmall      = FinovaBodySm,

    labelLarge     = FinovaLabelLg,
    labelMedium    = FinovaLabelMd,
    labelSmall     = FinovaLabelSm
)