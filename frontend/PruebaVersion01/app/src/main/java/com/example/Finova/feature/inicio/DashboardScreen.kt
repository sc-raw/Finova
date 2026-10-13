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
import com.example.Finova.core.design.components.*
import com.example.Finova.core.design.theme.*
import com.example.Finova.core.util.formatSoles

// ============================================================
// FINOVA — Dashboard (Inicio)
// Vertical 2 · feature/inicio
// Basado en Image 6.html — SOLO VISTA (datos hardcodeados)
// ============================================================

// --- Modelos locales solo para la vista ---
private data class MovimientoVisual(
    val titulo: String,
    val subtitulo: String,
    val monto: Double,
    val esIngreso: Boolean,
    val cuenta: String,
    val icono: ImageVector,
    val tieneIa: Boolean = false
)

@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    onScanClick: () -> Unit
) {
    // --- Datos hardcodeados (temporal) ---
    var saldosOcultos by remember { mutableStateOf(false) }

    val nombre = "Diego"
    val mes = "Marzo 2025"
    val saldoTotalSoles = 8450.20
    val saldoTotalUsd = 2253.38
    val tipoCambio = 3.75
    val ingresosMes = 4500.00
    val gastosMes = 1820.50
    val presupuestoPct = 74
    val presupuestoRestante = 1420.00
    val presupuestoTotal = 5500.00

    val movimientos = listOf(
        MovimientoVisual(
            titulo = "Supermercado Metro",
            subtitulo = "Alimentación · Hoy, 13:45",
            monto = 142.50,
            esIngreso = false,
            cuenta = "BCP ••4821",
            icono = Icons.Outlined.ShoppingCart,
            tieneIa = true
        ),
        MovimientoVisual(
            titulo = "Transferencia Sueldo",
            subtitulo = "Salario mensual · Ayer, 09:00",
            monto = 3800.00,
            esIngreso = true,
            cuenta = "BCP Soles",
            icono = Icons.Outlined.SouthWest
        ),
        MovimientoVisual(
            titulo = "Starbucks Coffee",
            subtitulo = "Café & Salidas · Ayer, 18:20",
            monto = 24.50,
            esIngreso = false,
            cuenta = "Efectivo",
            icono = Icons.Outlined.LocalCafe
        ),
        MovimientoVisual(
            titulo = "Recibo Luz Enel",
            subtitulo = "Servicios del hogar · 18 Feb",
            monto = 85.00,
            esIngreso = false,
            cuenta = "BCP ••4821",
            icono = Icons.Outlined.Bolt
        )
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavBar(
                currentRoute = "dashboard",
                onNavigate = onNavigate,
                onScanClick = onScanClick
            )
        }
    ) { padding ->
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
                    onCambiarDolares = { },
                    onPagarServicios = { },
                    onFinearCelular = { }
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

            items(movimientos.size) { index ->
                MovimientoRow(
                    mov = movimientos[index],
                    saldosOcultos = saldosOcultos
                )
                Spacer(Modifier.height(8.dp))
            }

            item {
                Spacer(Modifier.height(16.dp))
                PresupuestoMesCard(
                    porcentaje = presupuestoPct,
                    restante = presupuestoRestante,
                    total = presupuestoTotal
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

// ============================================================
// Componentes internos de la pantalla
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
                    icono = Icons.Outlined.ArrowDownward,
                    iconBg = FinovaSecondaryFixed.copy(alpha = 0.3f),
                    modifier = Modifier.weight(1f)
                )
                MiniStatCard(
                    label = "Gastos",
                    valor = "-${formatSoles(gastos)}",
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
                text = valor,
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
    onPagarServicios: () -> Unit,
    onFinearCelular: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OperacionItem("Transferir", Icons.Outlined.SwapHoriz, onTransferir, Modifier.weight(1f))
        OperacionItem("Cambiar a\nDólares", Icons.Outlined.CurrencyExchange, onCambiarDolares, Modifier.weight(1f))
        OperacionItem("Pagar\nservicios", Icons.Outlined.ReceiptLong, onPagarServicios, Modifier.weight(1f))
        OperacionItem("Finear a\ncelular", Icons.Outlined.Smartphone, onFinearCelular, Modifier.weight(1f))
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
private fun MovimientoRow(mov: MovimientoVisual, saldosOcultos: Boolean) {
    val iconTint = if (mov.esIngreso) FinovaSecondary else FinovaExpenseRed
    val iconBg = if (mov.esIngreso) FinovaSecondaryFixed.copy(alpha = 0.5f)
    else FinovaErrorContainer.copy(alpha = 0.6f)

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
                    text = if (saldosOcultos) "••••"
                    else "${if (mov.esIngreso) "+" else "-"}${formatSoles(mov.monto)}",
                    style = FinovaLabelLg,
                    color = if (mov.esIngreso) FinovaIncomeGreen else FinovaExpenseRed
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

@Composable
private fun PresupuestoMesCard(
    porcentaje: Int,
    restante: Double,
    total: Double
) {
    FinovaSoftCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        cornerRadius = 16.dp,
        padding = 16.dp,
        containerColor = FinovaSurfaceContainerLow
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { porcentaje / 100f },
                    modifier = Modifier.size(48.dp),
                    color = FinovaPrimary,
                    trackColor = FinovaSurfaceContainerHigh,
                    strokeWidth = 4.dp
                )
                Text(
                    text = "$porcentaje%",
                    style = FinovaLabelMd,
                    color = FinovaPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Presupuesto del mes",
                    style = FinovaLabelLg,
                    color = FinovaOnSurface
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Te quedan ${formatSoles(restante)} de ${formatSoles(total)}",
                    style = FinovaBodySm,
                    color = FinovaOnSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(FinovaSurfaceContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = FinovaPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}