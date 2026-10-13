package com.example.Finova.core.design.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.Finova.core.design.theme.*

// ============================================================
// FINOVA — Cards
// Fuente: DESIGN.md / Components / Cards & Containers
// ============================================================

/** Card base blanca con borde sutil y sombra suave (Nivel 1) */
@Composable
fun FinovaCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    padding: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val baseModifier = modifier.shadow(
        elevation = 2.dp,
        shape = shape,
        ambientColor = Color(0x0A0F172A),
        spotColor = Color(0x0A0F172A)
    )

    if (onClick != null) {
        Surface(
            modifier = baseModifier,
            shape = shape,
            color = FinovaSurfaceContainerLowest,
            border = BorderStroke(1.dp, FinovaBorder),
            onClick = onClick
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding),
                content = content
            )
        }
    } else {
        Surface(
            modifier = baseModifier,
            shape = shape,
            color = FinovaSurfaceContainerLowest,
            border = BorderStroke(1.dp, FinovaBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding),
                content = content
            )
        }
    }
}

/** Card de nivel 2 (flotante, menús, bottom sheets) — sombra más marcada */
@Composable
fun FinovaFloatingCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    padding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.shadow(
            elevation = 6.dp,
            shape = RoundedCornerShape(cornerRadius)
        ),
        shape = RoundedCornerShape(cornerRadius),
        color = FinovaSurfaceContainerLowest,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding),
                content = content
            )
        }
    )
}

/** Card sin borde (para fondos suaves como "Presupuesto del mes") */
@Composable
fun FinovaSoftCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    padding: Dp = 16.dp,
    containerColor: Color = FinovaSurfaceContainerLow,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    if (onClick != null) {
        Surface(
            modifier = modifier,
            shape = shape,
            color = containerColor,
            onClick = onClick
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding),
                content = content
            )
        }
    } else {
        Surface(
            modifier = modifier,
            shape = shape,
            color = containerColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding),
                content = content
            )
        }
    }
}