package com.example.Finova.core.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.Finova.core.design.theme.*

// ============================================================
// FINOVA — Botones
// Fuente: DESIGN.md / Components / Buttons
// ============================================================

/** Botón primario — pill, 52dp, teal oscuro, texto blanco */
@Composable
fun FinovaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FinovaPrimaryContainer,
            contentColor = FinovaOnPrimary,
            disabledContainerColor = FinovaPrimaryContainer.copy(alpha = 0.5f),
            disabledContentColor = FinovaOnPrimary.copy(alpha = 0.6f)
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = FinovaLabelLg,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** Botón secundario — fondo suave, texto primary */
@Composable
fun FinovaSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FinovaSurfaceContainer,
            contentColor = FinovaPrimary,
            disabledContainerColor = FinovaSurfaceContainer.copy(alpha = 0.5f),
            disabledContentColor = FinovaPrimary.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = FinovaLabelLg,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** Botón de texto / link (ej. "Ver cuentas", "Ver historial") */
@Composable
fun FinovaTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = FinovaLabelLg,
            color = FinovaPrimary
        )
        if (trailingIcon != null) {
            Spacer(Modifier.width(4.dp))
            trailingIcon()
        }
    }
}

/**
 * Chip rápido (ej. "+S/ 50", "+S/ 100", "Todo").
 * Usado en TransferirScreen (Image 2).
 * @param selected true = verde primary-container, false = fondo gris
 */
@Composable
fun FinovaChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false
) {
    val bg = if (selected) FinovaPrimaryFixed else FinovaSurfaceContainer
    val fg = if (selected) FinovaOnPrimaryFixedVariant else FinovaOnSurface

    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = FinovaLabelMd,
            color = fg,
            maxLines = 1
        )
    }
}

/** Chip preset para descripción (ej. "+ Ahorro semanal") */
@Composable
fun FinovaPresetChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(28.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(FinovaSurfaceContainer)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = FinovaLabelSm,
            color = FinovaOnSurfaceVariant,
            maxLines = 1
        )
    }
}