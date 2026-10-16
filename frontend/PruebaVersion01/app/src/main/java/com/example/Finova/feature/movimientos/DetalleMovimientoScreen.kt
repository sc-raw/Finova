package com.example.Finova.feature.movimientos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Finova.core.design.theme.*
import com.example.Finova.core.network.dto.MovimientoDto
import com.example.Finova.core.util.formatSoles

// ============================================================
// FINOVA — Detalle de Movimiento
// Vertical 3 · feature/movimientos
// ============================================================

@Composable
fun DetalleMovimientoScreen(
    movimientoId: Long,
    onBack: () -> Unit,
    viewModel: MovimientosViewModel = viewModel()
) {
    var iaExpandida by remember { mutableStateOf(true) }

    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()
    val movimiento by viewModel.movimientoDetalle.collectAsState()
    val eliminando by viewModel.eliminando.collectAsState()
    val eliminadoExitoso by viewModel.eliminadoExitoso.collectAsState()

    LaunchedEffect(movimientoId) {
        viewModel.cargarMovimientoDetalle(movimientoId)
    }

    LaunchedEffect(eliminadoExitoso) {
        if (eliminadoExitoso) {
            viewModel.resetearEliminado()
            onBack()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            DetalleTopBar(onBack = onBack)
        }
    ) { padding ->
        if (cargando && movimiento == null) {
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

        error?.let { msg ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = msg, color = FinovaError)
            }
            return@Scaffold
        }

        movimiento?.let { mov ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(12.dp))

                // ---------- Hero Card ----------
                HeroCardDetalle(mov)

                Spacer(Modifier.height(16.dp))

                // ---------- Detalle financiero ----------
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(FinovaSurfaceContainerLowest)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Detalle financiero",
                            style = FinovaLabelLg,
                            color = FinovaOnSurface
                        )
                        Text(
                            text = "Moneda local (PEN)",
                            style = FinovaLabelSm,
                            color = FinovaOnSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(10.dp))

                    FilaDetalle(
                        icono = Icons.Outlined.Restaurant,
                        iconBg = FinovaSecondaryContainer.copy(alpha = 0.6f),
                        iconTint = FinovaSecondary,
                        label = "Categoría",
                        valorChip = mov.categoriaNombre ?: "Sin categoría"
                    )
                    Spacer(Modifier.height(6.dp))
                    FilaDetalle(
                        icono = Icons.Outlined.AccountBalance,
                        iconBg = FinovaPrimary.copy(alpha = 0.1f),
                        iconTint = FinovaPrimary,
                        label = mov.cuentaOrigenNombre
                            ?: mov.cuentaDestinoNombre
                            ?: "Cuenta",
                        sublabel = "•••• ${(mov.cuentaOrigenId ?: 0L).toString().takeLast(4)}",
                        valorChip = mov.tipoMovimiento ?: "",
                        chipColor = FinovaSecondaryContainer.copy(alpha = 0.4f),
                        chipTextoColor = FinovaSecondary
                    )
                    Spacer(Modifier.height(6.dp))
                    FilaDetalle(
                        icono = Icons.Outlined.CreditCard,
                        iconBg = FinovaSurfaceContainerHigh,
                        iconTint = FinovaOnSurfaceVariant,
                        label = "Método de pago",
                        valorTexto = mov.metodoPagoNombre ?: "No especificado"
                    )
                    Spacer(Modifier.height(6.dp))
                    FilaDetalle(
                        icono = Icons.Outlined.Tag,
                        iconBg = FinovaSurfaceContainerHigh,
                        iconTint = FinovaOnSurfaceVariant,
                        label = "Operación TRX",
                        valorTexto = "#${mov.id.toString().padStart(6, '0')}",
                        trailingIcono = Icons.Outlined.ContentCopy
                    )
                }

                Spacer(Modifier.height(16.dp))

                // ---------- Luka Bot IA ----------
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(FinovaSurfaceContainerLow)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(FinovaTertiaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = FinovaOnTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Luka Bot Smart Audit",
                                    style = FinovaLabelLg,
                                    color = FinovaOnSurface
                                )
                                Spacer(Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(FinovaSecondaryContainer)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "98% conf.",
                                        style = FinovaLabelSm,
                                        color = FinovaOnSecondaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Extracción automática de comprobante",
                                style = FinovaBodySm,
                                color = FinovaOnSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(FinovaSurfaceContainer)
                                .clickable { iaExpandida = !iaExpandida },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (iaExpandida) Icons.Default.ExpandLess
                                else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = FinovaOnSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    if (iaExpandida) {
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MiniFiscalCard("Subtotal", formatSoles((mov.monto ?: 0.0) / 1.18), Modifier.weight(1f))
                            MiniFiscalCard("IGV (18%)", formatSoles((mov.monto ?: 0.0) * 0.18 / 1.18), Modifier.weight(1f))
                            MiniFiscalCard("RUC Emisor", "20600023451", Modifier.weight(1f))
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ---------- Botones ----------
                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FinovaPrimary,
                        contentColor = FinovaOnPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Compartir comprobante",
                        style = FinovaLabelLg
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FinovaSurfaceContainerHigh,
                            contentColor = FinovaOnSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = FinovaPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Editar",
                            style = FinovaLabelMd,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Button(
                        onClick = { viewModel.eliminarMovimiento(mov.id) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !eliminando,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FinovaErrorContainer.copy(alpha = 0.6f),
                            contentColor = FinovaError
                        )
                    ) {
                        if (eliminando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = FinovaError,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Eliminar",
                                style = FinovaLabelMd,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

// ============================================================
// Hero Card
// ============================================================

@Composable
private fun HeroCardDetalle(mov: MovimientoDto) {
    val esIngreso = mov.tipoMovimiento == "INGRESO"

    val icono: ImageVector = when {
        mov.tipoMovimiento == "TRANSFERENCIA" -> Icons.Outlined.SwapHoriz
        mov.tipoMovimiento == "APORTE_META" -> Icons.Outlined.Savings
        mov.tipoMovimiento == "RETIRO_META" -> Icons.Outlined.AccountBalanceWallet
        mov.categoriaNombre?.contains("Alimenta", ignoreCase = true) == true -> Icons.Outlined.ShoppingCart
        mov.categoriaNombre?.contains("Salud", ignoreCase = true) == true -> Icons.Outlined.Medication
        mov.categoriaNombre?.contains("Transporte", ignoreCase = true) == true -> Icons.Outlined.DirectionsCar
        mov.categoriaNombre?.contains("Servicios", ignoreCase = true) == true -> Icons.Outlined.Bolt
        esIngreso -> Icons.Outlined.SouthWest
        else -> Icons.Outlined.Receipt
    }

    val iconBg = if (esIngreso)
        FinovaSecondaryContainer.copy(alpha = 0.4f)
    else FinovaSurfaceContainerHigh

    val iconTint = if (esIngreso) FinovaSecondary else FinovaPrimary

    val colorMonto = if (esIngreso) FinovaSecondary else FinovaError
    val signo = if (esIngreso) "+" else "-"

    // Formatear fecha para mostrar
    val fechaMostrar = formatearFechaDetalle(mov.fechaMovimiento)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(FinovaSurfaceContainerLowest)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = mov.descripcion ?: "Movimiento",
            style = FinovaHeadlineMd,
            color = FinovaOnSurface
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = fechaMostrar,
            style = FinovaBodySm,
            color = FinovaOnSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "$signo${formatSoles(mov.monto ?: 0.0)}",
            style = FinovaDisplayLgMobile,
            color = colorMonto,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(FinovaSecondaryContainer.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = FinovaPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Completado",
                    style = FinovaLabelMd,
                    color = FinovaOnSecondaryFixedVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private fun formatearFechaDetalle(fechaISO: String?): String {
    if (fechaISO.isNullOrBlank()) return ""
    return try {
        val partes = fechaISO.split("T")
        val fecha = partes[0]
        val hora = if (partes.size > 1) partes[1].take(5) else ""

        val fechaPartes = fecha.split("-")
        if (fechaPartes.size < 3) return fechaISO

        val meses = listOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
        val mes = fechaPartes[1].toIntOrNull() ?: 1
        val dia = fechaPartes[2].toIntOrNull() ?: 1
        val anio = fechaPartes[0]

        val nombreMes = if (mes in 1..12) meses[mes - 1] else ""
        "$dia de $nombreMes de $anio · $hora"
    } catch (e: Exception) { "" }
}

// ============================================================
// Componentes
// ============================================================

@Composable
private fun DetalleTopBar(onBack: () -> Unit) {
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
                    contentDescription = "Más",
                    tint = FinovaOnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FilaDetalle(
    icono: ImageVector,
    iconBg: Color,
    iconTint: Color,
    label: String,
    sublabel: String? = null,
    valorChip: String? = null,
    chipColor: Color = FinovaSurfaceContainer,
    chipTextoColor: Color = FinovaOnSurface,
    valorTexto: String? = null,
    trailingIcono: ImageVector? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(FinovaSurfaceContainerLow)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = FinovaBodyMd,
                color = FinovaOnSurface,
                fontWeight = if (sublabel != null) FontWeight.Medium else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (sublabel != null) {
                Text(
                    text = sublabel,
                    style = FinovaBodySm,
                    color = FinovaOnSurfaceVariant
                )
            }
        }
        if (valorChip != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(chipColor)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = valorChip,
                    style = FinovaLabelMd,
                    color = chipTextoColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        if (valorTexto != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = valorTexto,
                    style = FinovaBodyMd,
                    color = FinovaOnSurface,
                    fontWeight = FontWeight.SemiBold
                )
                if (trailingIcono != null) {
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        imageVector = trailingIcono,
                        contentDescription = null,
                        tint = FinovaOnSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniFiscalCard(
    label: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(FinovaSurfaceContainerLowest)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = FinovaLabelSm,
            color = FinovaOnSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = valor,
            style = FinovaLabelMd,
            color = FinovaOnSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}