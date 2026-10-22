package com.example.Finova.feature.cuentas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Finova.core.design.components.*
import com.example.Finova.core.design.theme.*
import com.example.Finova.core.network.dto.CuentaDto
import com.example.Finova.core.util.formatSoles
import com.example.Finova.feature.movimientos.NuevaTransaccionSheet
import java.util.Locale

// ============================================================
// FINOVA — Mis Cuentas
// Vertical 2 · feature/cuentas
// ============================================================

private data class CuentaVisual(
    val titulo: String,
    val numero: String,
    val estado: String,
    val saldo: Double,
    val moneda: String,
    val icono: ImageVector,
    val iconBg: Color,
    val iconTint: Color
)

@Composable
fun MisCuentasScreen(
    onNavigate: (String) -> Unit,
    onTransferClick: () -> Unit,
    viewModel: CuentasViewModel = viewModel()
) {
    var mostrarSheet by remember { mutableStateOf(false) }
    var mostrarCrearCuenta by remember { mutableStateOf(false) }
    var mostrarEliminarCuenta by remember { mutableStateOf(false) }

    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()
    val cuentasDto by viewModel.cuentas.collectAsState()

    val cuentas: List<CuentaVisual> = cuentasDto.map { it.toVisual() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { CuentasTopBar() },
        bottomBar = {
            BottomNavBar(
                currentRoute = "mis_cuentas",
                onNavigate = onNavigate,
                onScanClick = { mostrarSheet = true }
            )
        }
    ) { padding ->
        if (cargando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = FinovaPrimary)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Text(
                    text = "Cuentas (${cuentas.size})",
                    style = FinovaHeadlineSm,
                    color = FinovaOnSurface,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            error?.let { msg ->
                item {
                    Surface(
                        color = FinovaErrorContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = msg,
                            style = FinovaBodySm,
                            color = FinovaError,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            if (cuentas.isEmpty()) {
                item {
                    Text(
                        text = "No tienes cuentas registradas",
                        style = FinovaBodySm,
                        color = FinovaOnSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                items(cuentas.size) { index ->
                    CuentaCard(cuenta = cuentas[index])
                    Spacer(Modifier.height(12.dp))
                }
            }

            item {
                OutlinedButton(
                    onClick = { mostrarEliminarCuenta = true },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(46.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Outlined.DeleteOutline, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Eliminar cuenta")
                }
                Spacer(Modifier.height(8.dp))
            }

            item {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { mostrarCrearCuenta = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FinovaSurfaceContainerLowest,
                            contentColor = FinovaPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Nueva cuenta",
                            style = FinovaLabelMd,
                            maxLines = 1
                        )
                    }

                    Button(
                        onClick = onTransferClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FinovaPrimary,
                            contentColor = FinovaOnPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SyncAlt,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Transferir",
                            style = FinovaLabelLg,
                            maxLines = 1
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (mostrarCrearCuenta) {
        CrearCuentaDialog(
            cuentasActuales = cuentasDto,
            onDismiss = { mostrarCrearCuenta = false },
            onComplete = {
                mostrarCrearCuenta = false
                viewModel.cargarCuentas()
            }
        )
    }

    if (mostrarEliminarCuenta) {
        EliminarCuentaDialog(
            cuentas = cuentasDto,
            onDismiss = { mostrarEliminarCuenta = false },
            onComplete = {
                mostrarEliminarCuenta = false
                viewModel.cargarCuentas()
            }
        )
    }

    // ---------- Bottom Sheet ----------
    if (mostrarSheet) {
        NuevaTransaccionSheet(
            onDismiss = { mostrarSheet = false },
            onEscanear = {
                mostrarSheet = false
                onNavigate("escanear")
            },
            onSubirImagen = {
                mostrarSheet = false
                onNavigate("escanear")
            },
            onAgregarManual = {
                mostrarSheet = false
                onNavigate("nuevo_movimiento")
            },
            onTransferir = {
                mostrarSheet = false
                onNavigate("transferir")
            }
        )
    }
}

// ============================================================
// Mapeo DTO → Visual
// ============================================================

private fun CuentaDto.toVisual(): CuentaVisual {
    val esUsd = moneda == "USD"

    val icono: ImageVector = when {
        esUsd -> Icons.Outlined.Savings
        nombre?.contains("Ahorro", ignoreCase = true) == true -> Icons.Outlined.Savings
        nombre?.contains("Sueldo", ignoreCase = true) == true -> Icons.Outlined.AccountBalance
        else -> Icons.Outlined.CreditCard
    }

    val iconBg = if (esUsd) FinovaTertiaryFixed.copy(alpha = 0.4f)
    else FinovaSecondaryContainer.copy(alpha = 0.6f)

    val iconTint = if (esUsd) FinovaTertiary else FinovaSecondary

    return CuentaVisual(
        titulo = nombre ?: "Cuenta",
        numero = "•••• ${numeroCuenta?.takeLast(4) ?: "----"}",
        estado = if (esUsd) "USD" else "Soles",
        saldo = saldoActual ?: 0.0,
        moneda = moneda ?: "PEN",
        icono = icono,
        iconBg = iconBg,
        iconTint = iconTint
    )
}

// ============================================================
// Componentes internos (SIN CAMBIOS)
// ============================================================

@Composable
private fun CuentasTopBar() {
    Surface(
        color = FinovaSurface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(FinovaPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "F",
                        style = FinovaHeadlineSm,
                        color = FinovaOnPrimary
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Mis Tarjetas",
                    style = FinovaTitleMd,
                    color = FinovaOnSurface
                )
            }
        }
    }
}

@Composable
private fun CuentaCard(cuenta: CuentaVisual) {
    FinovaCard(
        modifier = Modifier.padding(horizontal = 16.dp),
        padding = 16.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(cuenta.iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = cuenta.icono,
                    contentDescription = null,
                    tint = cuenta.iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cuenta.titulo,
                    style = FinovaTitleMd,
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
                    Text(
                        text = "•",
                        style = FinovaBodySm,
                        color = FinovaOnSurfaceVariant
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = cuenta.estado,
                        style = FinovaBodySm,
                        color = FinovaPrimary
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(FinovaSurfaceContainer)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Bancaria",
                    style = FinovaLabelSm,
                    color = FinovaOnSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        Column {
            Text(
                text = "Saldo disponible",
                style = FinovaLabelSm,
                color = FinovaOnSurfaceVariant
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = if (cuenta.moneda == "USD") "US$ ${String.format(Locale.US, "%.2f", cuenta.saldo)}" else formatSoles(cuenta.saldo),
                style = FinovaHeadlineMd,
                color = FinovaOnSurface
            )
        }
    }
}