package com.example.Finova.core.design.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ============================================================
// FINOVA — Theme
// Light-only por ahora (el diseño no contempla dark mode).
// ============================================================

private val FinovaLightColorScheme = lightColorScheme(
    // Primary
    primary              = FinovaPrimary,
    onPrimary            = FinovaOnPrimary,
    primaryContainer     = FinovaPrimaryContainer,
    onPrimaryContainer   = FinovaOnPrimaryContainer,
    inversePrimary       = FinovaInversePrimary,

    // Secondary
    secondary            = FinovaSecondary,
    onSecondary          = FinovaOnSecondary,
    secondaryContainer   = FinovaSecondaryContainer,
    onSecondaryContainer = FinovaOnSecondaryContainer,

    // Tertiary (AI)
    tertiary             = FinovaTertiary,
    onTertiary           = FinovaOnTertiary,
    tertiaryContainer    = FinovaTertiaryContainer,
    onTertiaryContainer  = FinovaOnTertiaryContainer,

    // Error
    error                = FinovaError,
    onError              = FinovaOnError,
    errorContainer       = FinovaErrorContainer,
    onErrorContainer     = FinovaOnErrorContainer,

    // Background / Surface
    background           = FinovaBackground,
    onBackground         = FinovaOnBackground,
    surface              = FinovaSurface,
    onSurface            = FinovaOnSurface,
    surfaceVariant       = FinovaSurfaceVariant,
    onSurfaceVariant     = FinovaOnSurfaceVariant,

    surfaceContainerLowest  = FinovaSurfaceContainerLowest,
    surfaceContainerLow     = FinovaSurfaceContainerLow,
    surfaceContainer        = FinovaSurfaceContainer,
    surfaceContainerHigh    = FinovaSurfaceContainerHigh,
    surfaceContainerHighest = FinovaSurfaceContainerHighest,

    surfaceDim           = FinovaSurfaceDim,
    surfaceBright        = FinovaSurfaceBright,
    surfaceTint          = FinovaSurfaceTint,

    inverseSurface       = FinovaInverseSurface,
    inverseOnSurface     = FinovaInverseOnSurface,

    // Outline
    outline              = FinovaOutline,
    outlineVariant       = FinovaOutlineVariant
)

@Composable
fun FinovaTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = FinovaLightColorScheme

    // Ajusta el color de la status bar al background del theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = FinovaTypography,
        content     = content
    )
}