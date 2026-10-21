package com.example.Finova.feature.movimientos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.SyncAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Finova.core.design.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaTransaccionSheet(
    onDismiss: () -> Unit,
    onEscanear: () -> Unit,
    onSubirImagen: () -> Unit,
    onAgregarManual: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = FinovaSurfaceContainerLowest,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(FinovaOutlineVariant.copy(alpha = 0.7f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            // ---------- Encabezado ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "¿Qué quieres registrar?",
                        style = FinovaHeadlineSm,
                        color = FinovaOnSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Elige la forma más rápida de ingresar tu movimiento",
                        style = FinovaBodySm,
                        color = FinovaOnSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(FinovaSurfaceContainerHigh.copy(alpha = 0.6f))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = FinovaOnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ---------- Opción 1: Escanear (destacada) ----------
            OpcionEscanear(onClick = onEscanear)

            Spacer(Modifier.height(10.dp))

            // ---------- Opción 2: Subir imagen ----------
            OpcionSimple(
                icono = Icons.Outlined.AddPhotoAlternate,
                iconBg = FinovaSecondaryContainer,
                iconTint = FinovaOnSecondaryContainer,
                titulo = "Subir imagen o captura",
                subtitulo = "Sube tu boleta desde tu galería",
                onClick = onSubirImagen
            )

            Spacer(Modifier.height(10.dp))

            // ---------- Opción 3: Manual ----------
            OpcionSimple(
                icono = Icons.Default.EditNote,
                iconBg = FinovaSurfaceContainerHighest,
                iconTint = FinovaOnSurfaceVariant,
                titulo = "Agregar manualmente",
                subtitulo = "Ingresa los montos",
                onClick = onAgregarManual
            )

            Spacer(Modifier.height(10.dp))



            // ---------- Nota de privacidad ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = FinovaPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Procesamiento seguro encriptado localmente",
                    style = FinovaLabelSm,
                    color = FinovaOnSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// ============================================================
// Componentes internos
// ============================================================

@Composable
private fun OpcionEscanear(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FinovaSurfaceContainerLow)
            .clickable(onClick = onClick)
    ) {
        // Badge "Recomendado"
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(FinovaTertiaryContainer)
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = FinovaOnTertiaryContainer,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Recomendado",
                    style = FinovaLabelSm,
                    color = FinovaOnTertiaryContainer,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FinovaTertiaryFixed),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = null,
                    tint = FinovaTertiary,
                    modifier = Modifier.size(26.dp)
                )
                // Pequeña estrella IA en la esquina
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = 4.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(FinovaTertiary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✨",
                        style = FinovaLabelSm,
                        color = FinovaOnTertiary
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f).padding(end = 70.dp)) {
                Text(
                    text = "Escanear boleta o factura",
                    style = FinovaTitleMd,
                    color = FinovaOnSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Cámara en vivo con detección automática de montos e ítems",
                    style = FinovaBodySm,
                    color = FinovaOnSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun OpcionSimple(
    icono: ImageVector,
    iconBg: Color,
    iconTint: Color,
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FinovaSurfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                style = FinovaTitleMd,
                color = FinovaOnSurface,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitulo,
                style = FinovaBodySm,
                color = FinovaOnSurfaceVariant,
                maxLines = 1
            )
        }
    }
}