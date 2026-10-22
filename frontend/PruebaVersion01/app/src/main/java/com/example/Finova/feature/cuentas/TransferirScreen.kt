package com.example.Finova.feature.cuentas

import androidx.compose.foundation.background
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Finova.core.design.components.FinovaCard
import com.example.Finova.core.design.theme.*
import com.example.Finova.core.network.dto.CuentaDto

// ============================================================
// FINOVA — Transferir
// Vertical 2 · feature/cuentas
// ============================================================

private data class CuentaTransfer(
    val id: Long,
    val titulo: String,
    val numero: String,
    val saldo: Double,
    val moneda: String,
    val esOrigen: Boolean
)

@Composable
fun TransferirScreen(
    onBack: () -> Unit,
    onTransferenciaExitosa: () -> Unit = {},
    viewModel: CuentasViewModel = viewModel()
) {
    // --- Datos del ViewModel ---
    val cuentasDto by viewModel.cuentas.collectAsState()
    val cargandoTransfer by viewModel.transferenciaCargando.collectAsState()
    val errorTransfer by viewModel.transferenciaError.collectAsState()
    val exitosoTransfer by viewModel.transferenciaExitosa.collectAsState()

    // --- Convertir DTOs a modelo visual ---
    val cuentasVisuales: List<CuentaTransfer> = cuentasDto.map { it.toCuentaTransfer() }

    // --- Estado: cuentas seleccionadas ---
    var cuentaOrigenId by remember { mutableStateOf<Long?>(null) }
    var cuentaDestinoId by remember { mutableStateOf<Long?>(null) }

    // --- Inicializar con las primeras cuentas cuando carguen ---
    LaunchedEffect(cuentasVisuales.size) {
        if (cuentasVisuales.isNotEmpty()) {
            if (cuentaOrigenId == null) cuentaOrigenId = cuentasVisuales[0].id
            if (cuentaDestinoId == null && cuentasVisuales.size > 1) {
                cuentaDestinoId = cuentasVisuales[1].id
            }
        }
    }

    val origen: CuentaTransfer? = cuentasVisuales.find { it.id == cuentaOrigenId }
        ?.copy(esOrigen = true)
    val destino: CuentaTransfer? = cuentasVisuales.find { it.id == cuentaDestinoId }
        ?.copy(esOrigen = false)

    // --- Otros estados ---
    var monto by remember { mutableStateOf("") }
    var concepto by remember { mutableStateOf("") }
    var rotacionSwap by remember { mutableFloatStateOf(0f) }
    var mostrarToast by remember { mutableStateOf(false) }
    var toastMensaje by remember { mutableStateOf("") }
    var toastEsError by remember { mutableStateOf(false) }
    var mostrarModalTransferencia by remember { mutableStateOf(false) }
    var modalTransferenciaEsError by remember { mutableStateOf(false) }
    var mensajeModalTransferencia by remember { mutableStateOf("") }

    var dropdownOrigenAbierto by remember { mutableStateOf(false) }
    var dropdownDestinoAbierto by remember { mutableStateOf(false) }

    val montoDouble = monto.toDoubleOrNull() ?: 0.0
    val excedeSaldo = origen != null && montoDouble > origen.saldo
    val saldoRestante = (origen?.saldo ?: 0.0) - montoDouble
    // Tipo de cambio temporal alineado con el valor que ya utiliza el Dashboard.
    val tipoCambio = 3.75
    val monedasDiferentes = origen != null && destino != null && origen.moneda != destino.moneda
    val montoConvertido = if (monedasDiferentes && origen != null) {
        if (origen.moneda == "USD") montoDouble * tipoCambio else montoDouble / tipoCambio
    } else montoDouble
    val simboloOrigen = simboloMoneda(origen?.moneda)
    val simboloDestino = simboloMoneda(destino?.moneda)

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

    // --- Reaccionar al resultado de la transferencia ---
    LaunchedEffect(exitosoTransfer) {
        if (exitosoTransfer) {
            mensajeModalTransferencia = "La transferencia se realizó correctamente."
            modalTransferenciaEsError = false
            mostrarModalTransferencia = true
            viewModel.resetearTransferencia()
            monto = ""
            concepto = ""
        }
    }

    LaunchedEffect(errorTransfer) {
        errorTransfer?.let {
            mensajeModalTransferencia = it
            modalTransferenciaEsError = true
            mostrarModalTransferencia = true
            viewModel.resetearTransferencia()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (mostrarModalTransferencia) {
            AlertDialog(
                onDismissRequest = {
                    mostrarModalTransferencia = false
                    if (!modalTransferenciaEsError) onTransferenciaExitosa()
                },
                icon = {
                    Icon(
                        imageVector = if (modalTransferenciaEsError) Icons.Default.ErrorOutline
                        else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (modalTransferenciaEsError) FinovaError else FinovaPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                },
                title = {
                    Text(
                        text = if (modalTransferenciaEsError) "Transferencia no realizada"
                        else "¡Transferencia exitosa!",
                        style = FinovaTitleMd
                    )
                },
                text = {
                    Text(mensajeModalTransferencia, style = FinovaBodyMd)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            mostrarModalTransferencia = false
                            if (!modalTransferenciaEsError) onTransferenciaExitosa()
                        }
                    ) {
                        Text("Entendido")
                    }
                },
                shape = RoundedCornerShape(24.dp),
                containerColor = FinovaSurface
            )
        }

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

                Spacer(Modifier.height(16.dp))

                // ---------- Card origen / swap / destino ----------
                if (origen != null && destino != null) {
                    CuentasCard(
                        origen = origen,
                        destino = destino,
                        rotacionSwap = rotacionSwap,
                        onSwap = {
                            rotacionSwap += 180f
                            val tempId = cuentaOrigenId
                            cuentaOrigenId = cuentaDestinoId
                            cuentaDestinoId = tempId
                            mostrarMensaje("Cuentas alternadas")
                        },
                        onOrigenClick = { dropdownOrigenAbierto = true },
                        onDestinoClick = { dropdownDestinoAbierto = true }
                    )
                } else if (cuentasVisuales.size < 2) {
                    Surface(
                        color = FinovaSurfaceContainerLow,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Necesitas al menos 2 cuentas para transferir",
                            style = FinovaBodyMd,
                            color = FinovaOnSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = FinovaPrimary)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ---------- Card monto ----------
                MontoCard(
                    monto = monto,
                    onMontoChange = { monto = it },
                    monedaOrigen = origen?.moneda ?: "PEN",
                    monedaDestino = destino?.moneda ?: "PEN",
                    montoConvertido = montoConvertido,
                    monedasDiferentes = monedasDiferentes,
                    tipoCambio = tipoCambio,
                    excedeSaldo = excedeSaldo,
                    onChipClick = { incremento ->
                        val actual = monto.toDoubleOrNull() ?: 0.0
                        monto = (actual + incremento).toBigDecimal()
                            .stripTrailingZeros().toPlainString()
                    },
                    onTodo = {
                        monto = (origen?.saldo ?: 0.0).toBigDecimal()
                            .stripTrailingZeros().toPlainString()
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
                ResumenCard(saldoRestante = saldoRestante, monedaOrigen = origen?.moneda ?: "PEN")

                Spacer(Modifier.height(16.dp))

                // ---------- Botones ----------
                Button(
                    onClick = {
                        when {
                            origen == null || destino == null -> {
                                mostrarMensaje("Selecciona las cuentas", true)
                            }
                            montoDouble <= 0 -> {
                                mostrarMensaje("Ingresa un monto válido", true)
                            }
                            excedeSaldo -> {
                                mostrarMensaje("Fondos insuficientes", true)
                            }
                            else -> {
                                viewModel.transferir(
                                    cuentaOrigenId = origen.id,
                                    cuentaDestinoId = destino.id,
                                    monto = montoDouble,
                                    descripcion = concepto
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FinovaPrimaryContainer,
                        contentColor = FinovaOnPrimary
                    ),
                    enabled = !cargandoTransfer
                ) {
                    if (cargandoTransfer) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = FinovaOnPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
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

        // ---------- Dropdown Origen ----------
        DropdownMenu(
            expanded = dropdownOrigenAbierto,
            onDismissRequest = { dropdownOrigenAbierto = false }
        ) {
            cuentasVisuales.forEach { c ->
                DropdownMenuItem(
                    text = {
                        Text("${c.titulo} - ${formatearMoneda(c.saldo, c.moneda)}")
                    },
                    onClick = {
                        if (c.id != cuentaDestinoId) {
                            cuentaOrigenId = c.id
                        } else {
                            mostrarMensaje("La cuenta origen no puede ser igual a la destino", true)
                        }
                        dropdownOrigenAbierto = false
                    }
                )
            }
        }

        // ---------- Dropdown Destino ----------
        DropdownMenu(
            expanded = dropdownDestinoAbierto,
            onDismissRequest = { dropdownDestinoAbierto = false }
        ) {
            cuentasVisuales
                .filter { it.id != cuentaOrigenId }
                .forEach { c ->
                    DropdownMenuItem(
                        text = {
                            Text("${c.titulo} - ${formatearMoneda(c.saldo, c.moneda)}")
                        },
                        onClick = {
                            cuentaDestinoId = c.id
                            dropdownDestinoAbierto = false
                        }
                    )
                }
        }

        // ---------- Toast ----------
        androidx.compose.animation.AnimatedVisibility(
            visible = mostrarToast,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp),
            enter = androidx.compose.animation.fadeIn() +
                    androidx.compose.animation.slideInVertically(initialOffsetY = { it }),
            exit = androidx.compose.animation.fadeOut() +
                    androidx.compose.animation.slideOutVertically(targetOffsetY = { it })
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
// Mapeo DTO → modelo de la vista
// ============================================================

private fun CuentaDto.toCuentaTransfer(): CuentaTransfer {
    return CuentaTransfer(
        id = id,
        titulo = nombre ?: "Cuenta",
        numero = "•••• ${numeroCuenta?.takeLast(4) ?: "----"}",
        saldo = saldoActual ?: 0.0,
        moneda = moneda ?: "PEN",
        esOrigen = true
    )
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
                text = "Transferir",
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
    onSwap: () -> Unit,
    onOrigenClick: () -> Unit,
    onDestinoClick: () -> Unit
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

        CuentaRow(cuenta = origen, esOrigen = true, onClick = onOrigenClick)

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

        CuentaRow(cuenta = destino, esOrigen = false, onClick = onDestinoClick)
    }
}

@Composable
private fun CuentaRow(
    cuenta: CuentaTransfer,
    esOrigen: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(FinovaSurfaceContainerLow)
            .clickable(onClick = onClick)
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
                    text = formatearMoneda(cuenta.saldo, cuenta.moneda),
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
    monedaOrigen: String,
    monedaDestino: String,
    montoConvertido: Double,
    monedasDiferentes: Boolean,
    tipoCambio: Double,
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
                    text = "Moneda: $monedaOrigen (${simboloMoneda(monedaOrigen)})",
                    style = FinovaLabelSm,
                    color = FinovaOnSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(FinovaSurfaceContainerLow.copy(alpha = 0.5f))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = simboloMoneda(monedaOrigen),
                    style = FinovaDisplayLgMobile,
                    color = FinovaPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(4.dp))

                BasicTextField(
                    value = monto,
                    onValueChange = { nuevo ->
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            QuickChip("+${simboloMoneda(monedaOrigen)} 50", Modifier.weight(1f)) { onChipClick(50.0) }
            QuickChip("+${simboloMoneda(monedaOrigen)} 100", Modifier.weight(1f)) { onChipClick(100.0) }
            QuickChip("+${simboloMoneda(monedaOrigen)} 500", Modifier.weight(1f)) { onChipClick(500.0) }
            QuickChip("Todo", Modifier.weight(1f), destacado = true, onClick = onTodo)
        }

        if (monedasDiferentes) {
            Spacer(Modifier.height(12.dp))
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(FinovaPrimary.copy(alpha = 0.08f)).padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Monto convertido a ${if (monedaDestino == "USD") "dólares" else "soles"}", style = FinovaLabelSm, color = FinovaOnSurfaceVariant)
                Text("${simboloMoneda(monedaDestino)} ${"%.2f".format(java.util.Locale.US, montoConvertido)}", style = FinovaHeadlineMd, color = FinovaPrimary, fontWeight = FontWeight.Bold)
                Text("Tipo de cambio referencial: 1 USD = S/ ${"%.2f".format(java.util.Locale.US, tipoCambio)}", style = FinovaBodySm, color = FinovaOnSurfaceVariant)
            }
        }

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

private fun simboloMoneda(moneda: String?): String = if (moneda == "USD") "US$" else "S/"

private fun formatearMoneda(monto: Double, moneda: String?): String =
    "${simboloMoneda(moneda)} ${"%.2f".format(java.util.Locale.US, monto)}"

@Composable
private fun ResumenCard(saldoRestante: Double, monedaOrigen: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FinovaSurfaceContainerLow)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ResumenRow(
                label = "Saldo estimado tras envío",
                valor = formatearMoneda(if (saldoRestante < 0) 0.0 else saldoRestante, monedaOrigen),
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