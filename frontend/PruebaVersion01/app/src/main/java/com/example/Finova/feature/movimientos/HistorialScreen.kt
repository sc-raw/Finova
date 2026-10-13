package com.example.Finova.feature.movimientos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.Finova.core.design.components.*
import com.example.Finova.core.design.theme.*
import com.example.Finova.core.util.formatSoles

// ============================================================
// FINOVA — Historial de Movimientos
// Vertical 3 · feature/movimientos
// Basado en Image 4.html — SOLO VISTA
// ============================================================

private data class MovimientoHistorial(
    val titulo: String,
    val subtitulo: String,
    val monto: Double,
    val esIngreso: Boolean,
    val cuenta: String,
    val icono: ImageVector,
    val tieneIa: Boolean = false
)

private data class GrupoFecha(
    val fecha: String,
    val totalDia: Double,
    val movimientos: List<MovimientoHistorial>
)

@Composable
fun HistorialScreen(
    onNavigate: (String) -> Unit,
    onMovimientoClick: () -> Unit
) {
    var busqueda by remember { mutableStateOf("") }
    var filtroSeleccionado by remember { mutableStateOf("Todos") }

    val grupos = listOf(
        GrupoFecha(
            fecha = "HOY · 28 OCTUBRE",
            totalDia = -70.30,
            movimientos = listOf(
                MovimientoHistorial(
                    titulo = "Supermercados Metro",
                    subtitulo = "Alimentación · 14:32",
                    monto = 45.80,
                    esIngreso = false,
                    cuenta = "BCP Débito",
                    icono = Icons.Outlined.ShoppingCart,
                    tieneIa = true
                ),
                MovimientoHistorial(
                    titulo = "Starbucks Coffee",
                    subtitulo = "Café y Salidas · 09:15",
                    monto = 24.50,
                    esIngreso = false,
                    cuenta = "Efectivo",
                    icono = Icons.Outlined.LocalCafe
                )
            )
        ),
        GrupoFecha(
            fecha = "AYER · 27 OCTUBRE",
            totalDia = 3762.00,
            movimientos = listOf(
                MovimientoHistorial(
                    titulo = "Transferencia Sueldo Empresa SAC",
                    subtitulo = "Ingreso / Salario · 18:00",
                    monto = 3800.00,
                    esIngreso = true,
                    cuenta = "BCP Ahorros",
                    icono = Icons.Outlined.Payments
                ),
                MovimientoHistorial(
                    titulo = "Farmacias Inkafarma",
                    subtitulo = "Salud y Cuidado · 11:20",
                    monto = 38.00,
                    esIngreso = false,
                    cuenta = "Interbank Débito",
                    icono = Icons.Outlined.Medication,
                    tieneIa = true
                )
            )
        ),
        GrupoFecha(
            fecha = "25 DE OCTUBRE",
            totalDia = 296.50,
            movimientos = listOf(
                MovimientoHistorial(
                    titulo = "Recibo de Luz Enel",
                    subtitulo = "Servicios del hogar · 16:45",
                    monto = 85.00,
                    esIngreso = false,
                    cuenta = "BCP Pago Serv.",
                    icono = Icons.Outlined.Bolt
                ),
                MovimientoHistorial(
                    titulo = "Uber Viaje",
                    subtitulo = "Transporte · 08:30",
                    monto = 18.50,
                    esIngreso = false,
                    cuenta = "BBVA Crédito",
                    icono = Icons.Outlined.DirectionsCar
                ),
                MovimientoHistorial(
                    titulo = "Pago de Freelance Diseño",
                    subtitulo = "Ingreso · 15:10",
                    monto = 400.00,
                    esIngreso = true,
                    cuenta = "Plin / BCP",
                    icono = Icons.Outlined.Palette,
                    tieneIa = true
                )
            )
        )
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { HistorialTopBar() },
        bottomBar = {
            BottomNavBar(
                currentRoute = "historial",
                onNavigate = onNavigate,
                onScanClick = { onNavigate("escanear") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // ---------- Título ----------
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Movimientos",
                        style = FinovaHeadlineLg,
                        color = FinovaOnSurface
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Historial consolidado de tus cuentas",
                        style = FinovaBodySm,
                        color = FinovaOnSurfaceVariant
                    )
                }
            }

            // ---------- Buscador ----------
            item {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(FinovaSurfaceContainerLowest)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = FinovaOnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    BasicTextField(
                        value = busqueda,
                        onValueChange = { busqueda = it },
                        modifier = Modifier.weight(1f),
                        textStyle = FinovaBodyMd.copy(color = FinovaOnSurface),
                        singleLine = true,
                        cursorBrush = SolidColor(FinovaPrimary),
                        decorationBox = { inner ->
                            Box {
                                if (busqueda.isEmpty()) {
                                    Text(
                                        text = "Buscar por comercio, monto o categoría...",
                                        style = FinovaBodyMd,
                                        color = FinovaOutline
                                    )
                                }
                                inner()
                            }
                        }
                    )
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = FinovaPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // ---------- Chips de filtro ----------
            item {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChipHistorial(
                        texto = "Todos",
                        seleccionado = filtroSeleccionado == "Todos",
                        onClick = { filtroSeleccionado = "Todos" }
                    )
                    FilterChipHistorial(
                        texto = "Gastos",
                        seleccionado = filtroSeleccionado == "Gastos",
                        dotColor = FinovaError,
                        onClick = { filtroSeleccionado = "Gastos" }
                    )
                    FilterChipHistorial(
                        texto = "Ingresos",
                        seleccionado = filtroSeleccionado == "Ingresos",
                        dotColor = FinovaSecondary,
                        onClick = { filtroSeleccionado = "Ingresos" }
                    )
                    FilterChipHistorial(
                        texto = "Cuentas (BCP, Efectivo)",
                        seleccionado = filtroSeleccionado == "Cuentas",
                        icono = Icons.Outlined.AccountBalance,
                        onClick = { filtroSeleccionado = "Cuentas" }
                    )
                    FilterChipHistorial(
                        texto = "Rango de fecha",
                        seleccionado = filtroSeleccionado == "Fecha",
                        icono = Icons.Outlined.CalendarToday,
                        onClick = { filtroSeleccionado = "Fecha" }
                    )
                }
            }

            // ---------- Grupos por fecha ----------
            grupos.forEach { grupo ->
                item {
                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (grupo.fecha.startsWith("HOY"))
                                            FinovaPrimaryContainer
                                        else FinovaOutlineVariant
                                    )
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = grupo.fecha,
                                style = FinovaLabelSm,
                                color = FinovaOnSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "${if (grupo.totalDia >= 0) "+" else ""}${formatSoles(grupo.totalDia)}",
                            style = FinovaLabelSm,
                            color = if (grupo.totalDia >= 0) FinovaSecondary
                            else FinovaOnSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }

                items(grupo.movimientos.size) { index ->
                    MovimientoHistorialRow(
                        mov = grupo.movimientos[index],
                        onClick = onMovimientoClick
                    )
                    Spacer(Modifier.height(4.dp))
                }
            }

            // ---------- Exportar ----------
            item {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Download,
                        contentDescription = null,
                        tint = FinovaPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Exportar reporte mensual (PDF / Excel)",
                        style = FinovaLabelMd,
                        color = FinovaPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun HistorialTopBar() {
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
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = FinovaOnSurface
                )
            }
            Spacer(Modifier.width(4.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Historial De Movimientos",
                    style = FinovaTitleMd,
                    color = FinovaOnSurface
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificaciones",
                    tint = FinovaOnSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(FinovaPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = FinovaOnPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
        }
    }
}

@Composable
private fun FilterChipHistorial(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    dotColor: Color? = null,
    icono: ImageVector? = null
) {
    val bg = if (seleccionado) FinovaPrimaryContainer else FinovaSurfaceContainerLowest
    val fg = if (seleccionado) FinovaOnPrimary else FinovaOnSurfaceVariant

    Row(
        modifier = Modifier
            .height(34.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (dotColor != null) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(Modifier.width(6.dp))
        }
        if (icono != null) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = if (seleccionado) FinovaOnPrimary else FinovaPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = texto,
            style = FinovaLabelMd,
            color = fg,
            fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
private fun MovimientoHistorialRow(
    mov: MovimientoHistorial,
    onClick: () -> Unit
) {
    val iconBg = if (mov.esIngreso) FinovaSecondaryContainer
    else FinovaSurfaceContainerHigh
    val iconTint = if (mov.esIngreso) FinovaSecondary else FinovaPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(FinovaSurfaceContainerLowest)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
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
                    style = FinovaTitleMd,
                    color = FinovaOnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (mov.tieneIa) {
                    Spacer(Modifier.width(6.dp))
                    IaBadgeHistorial()
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
                text = "${if (mov.esIngreso) "+" else "-"}${formatSoles(mov.monto)}",
                style = FinovaTitleMd,
                color = if (mov.esIngreso) FinovaSecondary else FinovaError,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = mov.cuenta,
                style = FinovaLabelSm,
                color = FinovaOutline
            )
        }
    }
}

@Composable
private fun IaBadgeHistorial() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(FinovaTertiaryFixed)
            .padding(horizontal = 5.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = FinovaOnTertiaryFixedVariant,
            modifier = Modifier.size(10.dp)
        )
        Spacer(Modifier.width(2.dp))
        Text(
            text = "IA",
            style = FinovaLabelSm,
            color = FinovaOnTertiaryFixedVariant
        )
    }
}