package com.example.Finova.feature.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Finova.core.design.components.*
import com.example.Finova.core.design.theme.*
import com.example.Finova.core.network.dto.MovimientoDto
import com.example.Finova.core.util.formatSoles
import com.example.Finova.feature.movimientos.NuevaTransaccionSheet

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts


// ============================================================
// FINOVA — Dashboard (Inicio)
// Vertical 2 · feature/inicio
// ============================================================

// --- Modelos locales solo para la vista ---
private data class MovimientoVisual(
    val titulo: String,
    val subtitulo: String,
    val monto: Double,
    val esIngreso: Boolean,
    val esTransferencia: Boolean,
    val cuenta: String,
    val icono: ImageVector,
    val tieneIa: Boolean = false
)

@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    onScanClick: () -> Unit,
    onImagenSeleccionada: (Uri) -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    // --- Estado del bottom sheet ---
    var mostrarSheet by remember { mutableStateOf(false) }
    var saldosOcultos by remember { mutableStateOf(false) }

    // --- Datos del ViewModel ---
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()
    val cuentas by viewModel.cuentas.collectAsState()
    val movimientosDto by viewModel.movimientos.collectAsState()

    // --- Mapear DTOs a modelo visual ---
    val movimientos: List<MovimientoVisual> = movimientosDto
        .sortedByDescending { it.fechaMovimiento ?: "" }
        .take(8)
        .map { dto -> dto.toVisual() }

    val nombre = viewModel.nombresUsuario.ifBlank { "Usuario" }
    val mes = "Marzo 2025" // TODO: usar Formatters para el mes actual
    val saldoTotalSoles = viewModel.saldoTotalSoles
    val saldoTotalUsd = viewModel.saldoTotalUsd
    val tipoCambio = viewModel.tipoCambio
    val ingresosMes = viewModel.ingresosMes
    val gastosMes = viewModel.gastosMes

    val selectorImagen = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onImagenSeleccionada(uri)
        }
    }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavBar(
                currentRoute = "dashboard",
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
                SaludoHeader(
                    nombre = nombre,
                    mes = mes,
                    saldosOcultos = saldosOcultos,
                    onToggleSaldos = { saldosOcultos = !saldosOcultos }
                )
            }

            // Errores (si los hay) sin romper el diseño
            error?.let { msg ->
                item {
                    Surface(
                        color = FinovaErrorContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp)
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

            item {
                Spacer(Modifier.height(16.dp))
                HeroSaldoCard(
                    saldoSoles = saldoTotalSoles,
                    saldoUsd = saldoTotalUsd,
                    tipoCambio = tipoCambio,
                    ingresos = ingresosMes,
                    gastos = gastosMes,
                    saldosOcultos = saldosOcultos
                )
            }

            item {
                Spacer(Modifier.height(24.dp))
                SeccionTitulo(
                    titulo = "Operaciones rápidas",
                    accionTexto = "Ver cuentas",
                    onAccionClick = { onNavigate("mis_cuentas") }
                )
                Spacer(Modifier.height(12.dp))
                OperacionesRapidas(
                    onTransferir = { onNavigate("transferir") },
                    onCambiarDolares = { /* La pantalla de cambio de dólares aún no está registrada en la navegación. */ },
                    onNuevoMovimiento = { onNavigate("nuevo_movimiento") },
                    onMisCuentas = { onNavigate("mis_cuentas") }
                )
            }

            item {
                Spacer(Modifier.height(24.dp))
                SeccionTitulo(
                    titulo = "Movimientos recientes",
                    accionTexto = "Ver historial",
                    onAccionClick = { onNavigate("historial") }
                )
                Spacer(Modifier.height(8.dp))
            }

            if (movimientos.isEmpty()) {
                item {
                    Text(
                        text = "No hay movimientos aún",
                        style = FinovaBodySm,
                        color = FinovaOnSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                items(movimientos.size) { index ->
                    MovimientoRow(
                        mov = movimientos[index]
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

        }
    }

    // ---------- Bottom Sheet de "Nueva Transacción" ----------
    if (mostrarSheet) {
        NuevaTransaccionSheet(
            onDismiss = { mostrarSheet = false },
            onEscanear = {
                mostrarSheet = false
                onScanClick()
            },
            onSubirImagen = {
                mostrarSheet = false
                selectorImagen.launch("image/*")
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

private fun MovimientoDto.toVisual(): MovimientoVisual {
    val esIngreso = tipoMovimiento == "INGRESO"

    val icono: ImageVector = when {
        tipoMovimiento == "TRANSFERENCIA" -> Icons.Outlined.SwapHoriz
        tipoMovimiento == "APORTE_META" -> Icons.Outlined.Savings
        tipoMovimiento == "RETIRO_META" -> Icons.Outlined.AccountBalanceWallet
        esIngreso -> Icons.Outlined.SouthWest
        else -> Icons.Outlined.NorthEast
    }

    val subtitulo = buildString {
        if (!categoriaNombre.isNullOrBlank()) {
            append(categoriaNombre)
            append(" · ")
        }
        append(fechaMovimiento?.take(10) ?: "")
    }

    return MovimientoVisual(
        titulo = titulo ?: descripcion ?: "Movimiento",
        subtitulo = subtitulo,
        monto = monto ?: 0.0,
        esIngreso = esIngreso,
        esTransferencia = tipoMovimiento == "TRANSFERENCIA",
        cuenta = cuentaOrigenNombre ?: cuentaDestinoNombre ?: "",
        icono = icono,
        tieneIa = false
    )
}

// ============================================================
// Componentes internos de la pantalla (SIN CAMBIOS)
// ============================================================

@Composable
private fun SaludoHeader(
    nombre: String,
    mes: String,
    saldosOcultos: Boolean,
    onToggleSaldos: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "¡Hola, $nombre!",
                    style = FinovaHeadlineMd,
                    color = FinovaOnSurface
                )
                Spacer(Modifier.width(6.dp))
                Text(text = "👋", style = FinovaHeadlineMd)
            }
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = null,
                    tint = FinovaSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = mes,
                    style = FinovaBodySm,
                    color = FinovaOnSurfaceVariant
                )
                Spacer(Modifier.width(6.dp))
                Text("•", style = FinovaBodySm, color = FinovaOutlineVariant)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Finanzas al día",
                    style = FinovaBodySm,
                    color = FinovaOnSurfaceVariant
                )
            }
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(FinovaSurfaceContainer),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onToggleSaldos, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = if (saldosOcultos) Icons.Outlined.VisibilityOff
                    else Icons.Outlined.Visibility,
                    contentDescription = "Alternar visibilidad",
                    tint = FinovaOnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun HeroSaldoCard(
    saldoSoles: Double,
    saldoUsd: Double,
    tipoCambio: Double,
    ingresos: Double,
    gastos: Double,
    saldosOcultos: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        FinovaPrimary,
                        FinovaPrimaryContainer,
                        FinovaOnSecondaryFixedVariant
                    )
                )
            )
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "SALDO TOTAL",
                        style = FinovaLabelSm,
                        color = FinovaOnPrimaryContainer
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = FinovaOnPrimaryContainer.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(FinovaPrimary.copy(alpha = 0.5f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "PEN / USD",
                        style = FinovaLabelSm,
                        color = FinovaOnPrimaryContainer
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "S/",
                    style = FinovaHeadlineMd,
                    color = FinovaOnPrimaryContainer.copy(alpha = 0.9f)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = if (saldosOcultos) "••••••"
                    else formatSoles(saldoSoles).removePrefix("S/ "),
                    style = FinovaDisplayLgMobile,
                    color = FinovaOnPrimary
                )
            }

            Spacer(Modifier.height(2.dp))

            Text(
                text = if (saldosOcultos) "≈ $ •••••• USD"
                else "≈ $ ${"%.2f".format(saldoUsd)} USD (T.C. $tipoCambio)",
                style = FinovaBodySm,
                color = FinovaOnPrimaryContainer.copy(alpha = 0.9f)
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = FinovaOnPrimary.copy(alpha = 0.1f))
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MiniStatCard(
                    label = "Ingresos",
                    valor = "+${formatSoles(ingresos)}",
                    oculto = saldosOcultos,
                    icono = Icons.Outlined.ArrowDownward,
                    iconBg = FinovaSecondaryFixed.copy(alpha = 0.3f),
                    modifier = Modifier.weight(1f)
                )
                MiniStatCard(
                    label = "Gastos",
                    valor = "-${formatSoles(gastos)}",
                    oculto = saldosOcultos,
                    icono = Icons.Outlined.ArrowUpward,
                    iconBg = FinovaError.copy(alpha = 0.3f),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MiniStatCard(
    label: String,
    valor: String,
    oculto: Boolean,
    icono: ImageVector,
    iconBg: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(FinovaSurfaceContainerLowest.copy(alpha = 0.15f))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = FinovaOnPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = FinovaLabelSm,
                color = FinovaOnPrimaryContainer.copy(alpha = 0.8f)
            )
            Text(
                text = if (oculto) "••••••" else valor,
                style = FinovaLabelLg,
                color = FinovaOnPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SeccionTitulo(
    titulo: String,
    accionTexto: String? = null,
    onAccionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titulo,
            style = FinovaHeadlineSm,
            color = FinovaOnSurface
        )
        if (accionTexto != null && onAccionClick != null) {
            FinovaTextButton(
                text = accionTexto,
                onClick = onAccionClick,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = FinovaPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        }
    }
}

@Composable
private fun OperacionesRapidas(
    onTransferir: () -> Unit,
    onCambiarDolares: () -> Unit,
    onNuevoMovimiento: () -> Unit,
    onMisCuentas: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OperacionItem("Transferir", Icons.Outlined.SwapHoriz, onTransferir, Modifier.weight(1f))
        OperacionItem("Cambiar\ndólares", Icons.Outlined.CurrencyExchange, onCambiarDolares, Modifier.weight(1f))
        OperacionItem("Nuevo\nmovimiento", Icons.Outlined.AddCircleOutline, onNuevoMovimiento, Modifier.weight(1f))
        OperacionItem("Mis\ncuentas", Icons.Outlined.AccountBalanceWallet, onMisCuentas, Modifier.weight(1f))
    }
}

@Composable
private fun OperacionItem(
    label: String,
    icono: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FinovaSoftCard(
        modifier = modifier,
        cornerRadius = 16.dp,
        padding = 12.dp,
        containerColor = FinovaSurfaceContainerLowest,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(FinovaSecondaryFixed.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = label,
                    tint = FinovaPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = label,
                style = FinovaLabelSm,
                color = FinovaOnSurface,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun MovimientoRow(mov: MovimientoVisual) {
    val iconTint = when {
        mov.esTransferencia -> Color(0xFFEF8C32)
        mov.esIngreso -> FinovaSecondary
        else -> FinovaExpenseRed
    }
    val iconBg = when {
        mov.esTransferencia -> Color(0xFFEF8C32).copy(alpha = 0.14f)
        mov.esIngreso -> FinovaSecondaryFixed.copy(alpha = 0.5f)
        else -> FinovaErrorContainer.copy(alpha = 0.6f)
    }

    FinovaCard(
        modifier = Modifier.padding(horizontal = 16.dp),
        padding = 14.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = mov.icono,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = mov.titulo,
                        style = FinovaLabelLg,
                        color = FinovaOnSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (mov.tieneIa) {
                        Spacer(Modifier.width(6.dp))
                        IaBadge()
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = mov.subtitulo,
                    style = FinovaBodySm,
                    color = FinovaOnSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = when {
                        mov.esTransferencia -> formatSoles(mov.monto)
                        mov.esIngreso -> "+${formatSoles(mov.monto)}"
                        else -> "-${formatSoles(mov.monto)}"
                    },
                    style = FinovaLabelLg,
                    color = when {
                        mov.esTransferencia -> Color(0xFFEF8C32)
                        mov.esIngreso -> FinovaIncomeGreen
                        else -> FinovaExpenseRed
                    }
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = mov.cuenta,
                    style = FinovaLabelSm,
                    color = FinovaOnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun IaBadge() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(FinovaAiAccent.copy(alpha = 0.12f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "Categorizado por IA",
            tint = FinovaAiAccent,
            modifier = Modifier.size(10.dp)
        )
        Spacer(Modifier.width(2.dp))
        Text(
            text = "IA",
            style = FinovaLabelSm,
            color = FinovaAiAccent
        )
    }
}
