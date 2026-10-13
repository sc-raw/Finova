package com.example.Finova.feature.cuentas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.Finova.core.design.components.FinovaCard
import com.example.Finova.core.design.theme.*
import com.example.Finova.core.util.formatSoles

// ============================================================
// FINOVA — Transferir
// Vertical 2 · feature/cuentas
// Basado en Image 2.html — SOLO VISTA
// ============================================================

private data class CuentaTransfer(
    val titulo: String,
    val numero: String,
    val saldo: Double,
    val esOrigen: Boolean
)

@Composable
fun TransferirScreen(
    onBack: () -> Unit
) {
    // --- Datos hardcodeados ---
    var origen by remember {
        mutableStateOf(
            CuentaTransfer(
                titulo = "BCP Soles - Sueldo",
                numero = "•••• 4821",
                saldo = 5820.00,
                esOrigen = true
            )
        )
    }
    var destino by remember {
        mutableStateOf(
            CuentaTransfer(
                titulo = "BCP Soles - Ahorros",
                numero = "•••• 9023",
                saldo = 2437.50,
                esOrigen = false
            )
        )
    }
    var monto by remember { mutableStateOf("") }
    var concepto by remember { mutableStateOf("") }
    var rotacionSwap by remember { mutableFloatStateOf(0f) }
    var mostrarToast by remember { mutableStateOf(false) }
    var toastMensaje by remember { mutableStateOf("") }
    var toastEsError by remember { mutableStateOf(false) }

    val montoDouble = monto.toDoubleOrNull() ?: 0.0
    val excedeSaldo = montoDouble > origen.saldo
    val saldoRestante = origen.saldo - montoDouble

    // --- Toast helper ---
    fun mostrarMensaje(msg: String, esError: Boolean = false) {
        toastMensaje = msg
        toastEsError = esError
        mostrarToast = true
    }

    // Auto-ocultar toast
    LaunchedEffect(mostrarToast) {
        if (mostrarToast) {
            kotlinx.coroutines.delay(2600)
            mostrarToast = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TransferirTopBar(onBack = onBack)
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                // ---------- Chip "Sin comisión" ----------
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(FinovaSurfaceContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(FinovaSecondary)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Sin comisión",
                                style = FinovaLabelSm,
                                color = FinovaOnSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ---------- Card origen / swap / destino ----------
                CuentasCard(
                    origen = origen,
                    destino = destino,
                    rotacionSwap = rotacionSwap,
                    onSwap = {
                        rotacionSwap += 180f
                        val temp = origen
                        origen = destino.copy(esOrigen = true)
                        destino = temp.copy(esOrigen = false)
                        mostrarMensaje("Cuentas alternadas")
                    }
                )

                Spacer(Modifier.height(16.dp))

                // ---------- Card monto ----------
                MontoCard(
                    monto = monto,
                    onMontoChange = { monto = it },
                    excedeSaldo = excedeSaldo,
                    onChipClick = { incremento ->
                        val actual = monto.toDoubleOrNull() ?: 0.0
                        monto = (actual + incremento).toBigDecimal().stripTrailingZeros().toPlainString()
                    },
                    onTodo = {
                        monto = origen.saldo.toBigDecimal().stripTrailingZeros().toPlainString()
                    }
                )

                Spacer(Modifier.height(16.dp))

                // ---------- Card concepto ----------
                ConceptoCard(
                    concepto = concepto,
                    onConceptoChange = { if (it.length <= 50) concepto = it },
                    onPresetClick = { concepto = it }
                )

                Spacer(Modifier.height(16.dp))

                // ---------- Card resumen ----------
                ResumenCard(saldoRestante = saldoRestante)

                Spacer(Modifier.height(16.dp))

                // ---------- Botones ----------
                Button(
                    onClick = {
                        when {
                            montoDouble <= 0 -> mostrarMensaje("Ingresa un monto válido", true)
                            excedeSaldo -> mostrarMensaje("Fondos insuficientes", true)
                            else -> mostrarMensaje("¡Transferiste ${formatSoles(montoDouble)}!")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FinovaPrimaryContainer,
                        contentColor = FinovaOnPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Transferir Ahora",
                        style = FinovaLabelLg
                    )
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FinovaSurfaceContainer,
                        contentColor = FinovaOnSurface
                    )
                ) {
                    Text(
                        text = "Cancelar",
                        style = FinovaLabelLg
                    )
                }

                Spacer(Modifier.height(32.dp))
            }
        }

        // ---------- Toast ----------
        androidx.compose.animation.AnimatedVisibility(
            visible = mostrarToast,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp),
            enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically(initialOffsetY = { it }),
            exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.slideOutVertically(targetOffsetY = { it })
        ) {
            Surface(
                color = FinovaInverseSurface,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (toastEsError) Icons.Default.ErrorOutline
                        else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (toastEsError) FinovaErrorContainer
                        else FinovaSecondaryFixed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = toastMensaje,
                        style = FinovaBodyMd,
                        color = FinovaInverseOnSurface
                    )
                }
            }
        }
    }
}

// ============================================================
// Componentes internos
// ============================================================

@Composable
private fun TransferirTopBar(onBack: () -> Unit) {
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
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Detalle De Movimiento",
                style = FinovaTitleMd,
                color = FinovaOnSurface,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Más opciones",
                    tint = FinovaOnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CuentasCard(
    origen: CuentaTransfer,
    destino: CuentaTransfer,
    rotacionSwap: Float,
    onSwap: () -> Unit
) {
    FinovaCard(padding = 16.dp) {

        // --- Origen ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(FinovaPrimary)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "De: Cuenta Origen",
                    style = FinovaLabelMd,
                    color = FinovaOnSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(FinovaPrimaryFixed.copy(alpha = 0.4f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Débito automático",
                    style = FinovaLabelSm,
                    color = FinovaPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        CuentaRow(cuenta = origen, esOrigen = true)

        // --- Botón swap ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            contentAlignment = Alignment.Center
        ) {
            HorizontalDivider(color = FinovaSurfaceContainerHighest)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(FinovaPrimary)
                    .clickable(onClick = onSwap),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = "Intercambiar cuentas",
                    tint = FinovaOnPrimary,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(rotacionSwap)
                )
            }
        }

        // --- Destino ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(FinovaSecondary)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Para: Cuenta Destino",
                    style = FinovaLabelMd,
                    color = FinovaOnSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(FinovaSecondaryFixed.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Recepción instantánea",
                    style = FinovaLabelSm,
                    color = FinovaSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        CuentaRow(cuenta = destino, esOrigen = false)
    }
}

@Composable
private fun CuentaRow(cuenta: CuentaTransfer, esOrigen: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(FinovaSurfaceContainerLow)
            .clickable { }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (esOrigen) FinovaPrimary else FinovaSecondary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (esOrigen) Icons.Outlined.AccountBalance
                else Icons.Outlined.Savings,
                contentDescription = null,
                tint = FinovaOnPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cuenta.titulo,
                style = FinovaHeadlineSm,
                color = FinovaOnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = cuenta.numero,
                    style = FinovaBodySm,
                    color = FinovaOnSurfaceVariant
                )
                Spacer(Modifier.width(6.dp))
                Text("•", style = FinovaBodySm, color = FinovaOnSurfaceVariant)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (esOrigen) "Disponible: " else "Saldo actual: ",
                    style = FinovaBodySm,
                    color = FinovaOnSurfaceVariant
                )
                Text(
                    text = formatSoles(cuenta.saldo),
                    style = FinovaBodySm,
                    color = FinovaOnSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }
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
private fun MontoCard(
    monto: String,
    onMontoChange: (String) -> Unit,
    excedeSaldo: Boolean,
    onChipClick: (Double) -> Unit,
    onTodo: () -> Unit
) {
    FinovaCard(padding = 16.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Monto a transferir",
                style = FinovaLabelMd,
                color = FinovaOnSurfaceVariant
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(FinovaSurfaceContainer)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Moneda: PEN (S/)",
                    style = FinovaLabelSm,
                    color = FinovaOnSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // --- Input grande ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(FinovaSurfaceContainerLow.copy(alpha = 0.5f))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "S/",
                    style = FinovaDisplayLgMobile,
                    color = FinovaPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(4.dp))

                BasicTextField(
                    value = monto,
                    onValueChange = { nuevo ->
                        // solo dígitos y un punto
                        if (nuevo.isEmpty() || nuevo.matches(Regex("^\\d*\\.?\\d*$"))) {
                            onMontoChange(nuevo)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp),
                    textStyle = FinovaDisplayLgMobile.copy(
                        color = if (excedeSaldo) FinovaError else FinovaOnSurface,
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
                                    color = FinovaOutlineVariant.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            inner()
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // --- Chips rápidos ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            QuickChip("+S/ 50", Modifier.weight(1f)) { onChipClick(50.0) }
            QuickChip("+S/ 100", Modifier.weight(1f)) { onChipClick(100.0) }
            QuickChip("+S/ 500", Modifier.weight(1f)) { onChipClick(500.0) }
            QuickChip("Todo", Modifier.weight(1f), destacado = true, onClick = onTodo)
        }

        // --- Warning de saldo ---
        if (excedeSaldo) {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(FinovaErrorContainer.copy(alpha = 0.4f))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = FinovaError,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "El monto excede el saldo disponible de la cuenta origen.",
                    style = FinovaBodySm,
                    color = FinovaError
                )
            }
        }
    }
}

@Composable
private fun QuickChip(
    text: String,
    modifier: Modifier = Modifier,
    destacado: Boolean = false,
    onClick: () -> Unit
) {
    val bg = if (destacado) FinovaPrimaryFixed else FinovaSurfaceContainer
    val fg = if (destacado) FinovaOnPrimaryFixedVariant else FinovaOnSurface

    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = FinovaLabelMd,
            color = fg,
            fontWeight = if (destacado) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
private fun ConceptoCard(
    concepto: String,
    onConceptoChange: (String) -> Unit,
    onPresetClick: (String) -> Unit
) {
    FinovaCard(padding = 16.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Descripción o motivo (opcional)",
                style = FinovaLabelMd,
                color = FinovaOnSurfaceVariant
            )
            Text(
                text = "${concepto.length}/50",
                style = FinovaLabelSm,
                color = FinovaOutline
            )
        }

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(FinovaSurfaceContainerLow)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = concepto,
                onValueChange = onConceptoChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = FinovaBodyMd.copy(color = FinovaOnSurface),
                singleLine = true,
                cursorBrush = SolidColor(FinovaPrimary),
                decorationBox = { inner ->
                    Box {
                        if (concepto.isEmpty()) {
                            Text(
                                text = "Ej. Ahorro del mes, pago de tarjeta...",
                                style = FinovaBodyMd,
                                color = FinovaOutline
                            )
                        }
                        inner()
                    }
                }
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PresetChipSmall("+ Ahorro semanal") { onPresetClick("Ahorro semanal") }
            PresetChipSmall("+ Emergencia") { onPresetClick("Fondo de emergencia") }
            PresetChipSmall("+ Servicios") { onPresetClick("Alquiler / Servicios") }
        }
    }
}

@Composable
private fun PresetChipSmall(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
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

@Composable
private fun ResumenCard(saldoRestante: Double) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FinovaSurfaceContainerLow)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ResumenRow(
                label = "Horario de procesamiento",
                valor = "Inmediato (24/7)"
            )
            ResumenRow(
                label = "Comisión bancaria",
                valor = "Gratis (S/ 0.00)",
                valorColor = FinovaSecondary,
                valorBold = true
            )
            ResumenRow(
                label = "Saldo estimado tras envío",
                valor = formatSoles(if (saldoRestante < 0) 0.0 else saldoRestante),
                valorBold = true
            )
        }
    }
}

@Composable
private fun ResumenRow(
    label: String,
    valor: String,
    valorColor: Color = FinovaOnSurface,
    valorBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = FinovaBodySm,
            color = FinovaOnSurfaceVariant
        )
        Text(
            text = valor,
            style = FinovaBodySm,
            color = valorColor,
            fontWeight = if (valorBold) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}