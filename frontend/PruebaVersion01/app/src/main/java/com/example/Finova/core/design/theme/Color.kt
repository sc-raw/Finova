package com.example.Finova.core.design.theme

import androidx.compose.ui.graphics.Color

// ============================================================
// FINOVA — Paleta de colores
// Fuente: DESIGN.md (bloque YAML) + semánticos de la sección "Colors"
// ============================================================

// ---------- Primary (Dark Teal — ancla de marca) ----------
val FinovaPrimary              = Color(0xFF005C55) // #005c55
val FinovaOnPrimary            = Color(0xFFFFFFFF) // #ffffff
val FinovaPrimaryContainer     = Color(0xFF0F766E) // #0f766e
val FinovaOnPrimaryContainer   = Color(0xFFA3FAEF) // #a3faef
val FinovaInversePrimary       = Color(0xFF80D5CB) // #80d5cb

// ---------- Secondary (Deep Pine Green) ----------
val FinovaSecondary            = Color(0xFF2A6959) // #2a6959
val FinovaOnSecondary          = Color(0xFFFFFFFF)
val FinovaSecondaryContainer   = Color(0xFFADEDD8) // #adedd8
val FinovaOnSecondaryContainer = Color(0xFF2F6D5D) // #2f6d5d

// ---------- Tertiary (AI Accent / Purple) ----------
val FinovaTertiary             = Color(0xFF4E3FAF) // #4e3faf
val FinovaOnTertiary           = Color(0xFFFFFFFF)
val FinovaTertiaryContainer    = Color(0xFF6659C9) // #6659c9
val FinovaOnTertiaryContainer  = Color(0xFFECE7FF) // #ece7ff

// ---------- Error ----------
val FinovaError                = Color(0xFFBA1A1A) // #ba1a1a
val FinovaOnError              = Color(0xFFFFFFFF)
val FinovaErrorContainer       = Color(0xFFFFDAD6) // #ffdad6
val FinovaOnErrorContainer     = Color(0xFF93000A) // #93000a

// ---------- Surface / Background ----------
val FinovaBackground           = Color(0xFFFAF8FF) // #faf8ff
val FinovaOnBackground         = Color(0xFF131B2E) // #131b2e
val FinovaSurface              = Color(0xFFFAF8FF) // #faf8ff
val FinovaOnSurface            = Color(0xFF131B2E) // #131b2e
val FinovaSurfaceVariant       = Color(0xFFDAE2FD) // #dae2fd
val FinovaOnSurfaceVariant     = Color(0xFF3E4947) // #3e4947
val FinovaSurfaceContainerLowest  = Color(0xFFFFFFFF) // #ffffff
val FinovaSurfaceContainerLow     = Color(0xFFF2F3FF) // #f2f3ff
val FinovaSurfaceContainer        = Color(0xFFEAEDFF) // #eaedff
val FinovaSurfaceContainerHigh    = Color(0xFFE2E7FF) // #e2e7ff
val FinovaSurfaceContainerHighest = Color(0xFFDAE2FD) // #dae2fd
val FinovaSurfaceDim           = Color(0xFFD2D9F4) // #d2d9f4
val FinovaSurfaceBright        = Color(0xFFFAF8FF) // #faf8ff
val FinovaSurfaceTint          = Color(0xFF006A63) // #006a63
val FinovaInverseSurface       = Color(0xFF283044) // #283044
val FinovaInverseOnSurface     = Color(0xFFEEF0FF) // #eef0ff

// ---------- Outline ----------
val FinovaOutline              = Color(0xFF6E7977) // #6e7977
val FinovaOutlineVariant       = Color(0xFFBDC9C6) // #bdc9c6

// ---------- Fixed (Primary) ----------
val FinovaPrimaryFixed         = Color(0xFF9CF2E8) // #9cf2e8
val FinovaPrimaryFixedDim      = Color(0xFF80D5CB) // #80d5cb
val FinovaOnPrimaryFixed       = Color(0xFF00201D) // #00201d
val FinovaOnPrimaryFixedVariant= Color(0xFF00504A) // #00504a

// ---------- Fixed (Secondary) ----------
val FinovaSecondaryFixed       = Color(0xFFB0EFDB) // #b0efdb
val FinovaSecondaryFixedDim    = Color(0xFF94D3BF) // #94d3bf
val FinovaOnSecondaryFixed     = Color(0xFF002019) // #002019
val FinovaOnSecondaryFixedVariant = Color(0xFF095041) // #095041

// ---------- Fixed (Tertiary / AI) ----------
val FinovaTertiaryFixed        = Color(0xFFE4DFFF) // #e4dfff
val FinovaTertiaryFixedDim     = Color(0xFFC7BFFF) // #c7bfff
val FinovaOnTertiaryFixed      = Color(0xFF170065) // #170065
val FinovaOnTertiaryFixedVariant = Color(0xFF4333A3) // #4333a3

// ============================================================
// SEMÁNTICOS FINANCIEROS (no son tokens M3, se usan directo)
// ============================================================

/** Rojo para egresos, alertas de pago, variaciones negativas */
val FinovaExpenseRed       = Color(0xFFDC2626) // #DC2626

/** Verde para ingresos, depósitos, aumentos de saldo */
val FinovaIncomeGreen      = Color(0xFF16A34A) // #16A34A

/** Acento púrpura reservado SOLO para IA / OCR / insights automáticos */
val FinovaAiAccent         = Color(0xFF7C6FE0) // #7C6FE0

/** Teal claro del FAB "Escanear", focus outlines, progress */
val FinovaAccentLightTeal  = Color(0xFF14B8A6) // #14B8A6

/** Texto primario profundo (headers, montos) */
val FinovaTextPrimary      = Color(0xFF0F172A) // #0F172A

/** Texto secundario (timestamps, subtítulos) */
val FinovaTextSecondary    = Color(0xFF565C66) // #565C66

/** Bordes y divisores */
val FinovaBorder           = Color(0xFFE2E8F0) // #E2E8F0