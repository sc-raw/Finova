package com.example.Finova.feature.estadisticas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.Finova.core.design.theme.*

// ============================================================
// FINOVA — Añadir Fondos a Meta
// Vertical 5 · feature/estadisticas
// Basado en Image 6.html — SOLO VISTA
// ============================================================

@Composable
fun AnadirFondosScreen(
    onBack: () -> Unit,
    onConfirmar: () -> Unit
) {
    var monto by remember { mutableStateOf("150.00") }
    var descripcion by remember { mutableStateOf("") }
    var mostrarExito by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { AnadirFondosTopBar(onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // ---------- Pill "Aporte a tus objetivos" ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(FinovaSecondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Savings,
                            contentDescription = null,
                            tint = FinovaOnSecondaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Aporte a tus objetivos",
                        style = FinovaLabelLg,
                        color = FinovaPrimaryContainer
                    )
                }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(FinovaSurfaceContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = FinovaPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Cifrado BCRP",
                        style = FinovaLabelSm,
                        color = FinovaOnSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ---------- Cuenta origen ----------
            SelectorLabel(label = "Selecciona tu tarjeta o cuenta", trailing = "Origen")
            Spacer(Modifier.height(6.dp))
            CuentaSelectorCard(
                titulo = "BCP Soles - Débito",
                numero = "•••• 4821",
                saldo = "Saldo disponible: S/ 5,820.00",
                icono = Icons.Outlined.AccountBalance
            )

            Spacer(Modifier.height(16.dp))

            // ---------- Meta destino ----------
            SelectorLabel(label = "Selecciona la meta", trailing = "Destino")
            Spacer(Modifier.height(6.dp))
            MetaSelectorCard(
                nombre = "Fondo de emergencia",
                monto = "$2,400 acumulado de $3,000",
                porcentaje = 80
            )

            Spacer(Modifier.height(16.dp))

            // ---------- Monto a ingresar ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Monto a ingresar",
                    style = FinovaLabelLg,
                    color = FinovaOnSurface
                )
                Text(
                    text = "Sin comisiones",
                    style = FinovaLabelSm,
                    color = FinovaPrimaryContainer,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(6.dp))

            // Input grande con prefijo S/
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(FinovaSurfaceContainerLowest)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "S/",
                    style = FinovaDisplayLgMobile,
                    color = FinovaPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(4.dp))
                BasicTextField(
                    value = monto,
                    onValueChange = { nuevo ->
                        if (nuevo.isEmpty() || nuevo.matches(Regex("^\\d*\\.?\\d*$"))) {
                            monto = nuevo
                        }
                    },
                    modifier = Modifier.weight(1f),
                    textStyle = FinovaDisplayLgMobile.copy(
                        color = FinovaOnSurface,
                        fontWeight = FontWeight.Bold
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(FinovaPrimary),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    decorationBox = { inner ->
                        Box {
                            if (monto.isEmpty()) {
                                Text(
                                    text = "0.00",
                                    style = FinovaDisplayLgMobile,
                                    color = FinovaOutlineVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            inner()
                        }
                    }
                )
            }

            Spacer(Modifier.height(8.dp))

            // Chips rápidos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rápido:",
                    style = FinovaLabelSm,
                    color = FinovaOutline
                )
                QuickChip("+S/ 50") { sumarAlMonto(50.0, monto) { monto = it } }
                QuickChip("+S/ 100") { sumarAlMonto(100.0, monto) { monto = it } }
                QuickChip("+S/ 200") { sumarAlMonto(200.0, monto) { monto = it } }
            }

            Spacer(Modifier.height(20.dp))

            // ---------- Descripción ----------
            Text(
                text = "Descripción",
                style = FinovaLabelLg,
                color = FinovaOnSurface
            )
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FinovaSurfaceContainerLowest)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.EditNote,
                    contentDescription = null,
                    tint = FinovaOutline,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                BasicTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    modifier = Modifier.weight(1f),
                    textStyle = FinovaBodyMd.copy(color = FinovaOnSurface),
                    singleLine = true,
                    cursorBrush = SolidColor(FinovaPrimary),
                    decorationBox = { inner ->
                        Box {
                            if (descripcion.isEmpty()) {
                                Text(
                                    text = "Ej. Ahorro de la quincena, bono extra...",
                                    style = FinovaBodyMd,
                                    color = FinovaOutline
                                )
                            }
                            inner()
                        }
                    }
                )
            }

            Spacer(Modifier.height(24.dp))

            // ---------- Botones ----------
            Button(
                onClick = { mostrarExito = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FinovaPrimaryContainer,
                    contentColor = FinovaOnPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Confirmar depósito",
                    style = FinovaLabelLg,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FinovaSurfaceContainerLow,
                    contentColor = FinovaPrimaryContainer
                )
            ) {
                Text(
                    text = "Cancelar",
                    style = FinovaLabelLg,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    // ---------- Modal de éxito ----------
    if (mostrarExito) {
        AlertDialog(
            onDismissRequest = { mostrarExito = false },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarExito = false
                        onConfirmar()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FinovaPrimaryContainer,
                        contentColor = FinovaOnPrimary
                    )
                ) {
                    Text("Entendido", style = FinovaLabelLg)
                }
            },
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(FinovaSecondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = FinovaOnSecondaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "¡Depósito exitoso!",
                    style = FinovaHeadlineMd,
                    color = FinovaOnSurface
                )
            },
            text = {
                Text(
                    text = "Se han añadido S/ ${monto.ifEmpty { "0.00" }} a tu meta de ahorro.",
                    style = FinovaBodyMd,
                    color = FinovaOnSurfaceVariant
                )
            }
        )
    }
}

// ============================================================
// Componentes internos
// ============================================================

@Composable
private fun AnadirFondosTopBar(onBack: () -> Unit) {
    Surface(
        color = FinovaSurface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = FinovaOnSurface
                )
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(FinovaPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "F",
                    style = FinovaLabelLg,
                    color = FinovaOnPrimary
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Añadir Fondos",
                style = FinovaHeadlineSm,
                color = FinovaOnSurface,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Más",
                    tint = FinovaOnSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun SelectorLabel(label: String, trailing: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = FinovaLabelLg,
            color = FinovaOnSurface
        )
        Text(
            text = trailing,
            style = FinovaLabelSm,
            color = FinovaOnSurfaceVariant
        )
    }
}

@Composable
private fun CuentaSelectorCard(
    titulo: String,
    numero: String,
    saldo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FinovaSurfaceContainerLowest)
            .clickable { }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(FinovaSurfaceContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = FinovaPrimaryContainer,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = titulo,
                    style = FinovaHeadlineSm,
                    color = FinovaOnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(FinovaSurfaceContainer)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = numero,
                        style = FinovaLabelSm,
                        color = FinovaOnSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = saldo,
                style = FinovaBodySm,
                color = FinovaOnSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.ExpandMore,
            contentDescription = null,
            tint = FinovaOnSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun MetaSelectorCard(
    nombre: String,
    monto: String,
    porcentaje: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FinovaSurfaceContainerLowest)
            .clickable { }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FinovaSecondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = FinovaSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nombre,
                    style = FinovaHeadlineSm,
                    color = FinovaOnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = monto,
                    style = FinovaBodySm,
                    color = FinovaOnSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = null,
                tint = FinovaOnSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Progreso actual",
                style = FinovaLabelSm,
                color = FinovaOnSurfaceVariant
            )
            Text(
                text = "$porcentaje%",
                style = FinovaLabelSm,
                color = FinovaPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(FinovaSurfaceContainer)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(porcentaje / 100f)
                    .clip(RoundedCornerShape(999.dp))
                    .background(FinovaPrimaryContainer)
            )
        }
    }
}

@Composable
private fun QuickChip(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(32.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(FinovaSurfaceContainer)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = FinovaLabelMd,
            color = FinovaOnSurface,
            maxLines = 1
        )
    }
}

private fun sumarAlMonto(incremento: Double, actual: String, onUpdate: (String) -> Unit) {
    val valorActual = actual.toDoubleOrNull() ?: 0.0
    val nuevo = valorActual + incremento
    onUpdate("%.2f".format(nuevo))
}