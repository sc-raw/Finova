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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Finova.core.design.components.*
import com.example.Finova.core.design.theme.*
import com.example.Finova.core.network.dto.MovimientoDto
import com.example.Finova.core.util.formatSoles

// ============================================================
// FINOVA — Historial de Movimientos
// Vertical 3 · feature/movimientos
// ============================================================

private data class MovimientoHistorial(
    val id: Long,
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
    onMovimientoClick: (Long) -> Unit,
    viewModel: MovimientosViewModel = viewModel()
) {
    var mostrarSheet by remember { mutableStateOf(false) }

    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()
    val movimientosDto by viewModel.movimientosFiltrados.collectAsState()
    val busqueda by viewModel.textoBusqueda.collectAsState()
    val filtroActual by viewModel.filtroActual.collectAsState()

    // Mapear DTOs a modelo visual
    val movimientosVisuales = movimientosDto.map { it.toHistorialVisual() }

    // Agrupar por fecha
    val grupos: List<GrupoFecha> = agruparPorFecha(movimientosVisuales, movimientosDto)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { HistorialTopBar() },
        bottomBar = {
            BottomNavBar(
                currentRoute = "historial",
                onNavigate = onNavigate,
                onScanClick = { mostrarSheet = true }
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
                        onValueChange = { viewModel.actualizarBusqueda(it) },
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
                        seleccionado = filtroActual == FiltroMovimiento.TODOS,
                        onClick = { viewModel.cambiarFiltro(FiltroMovimiento.TODOS) }
                    )
                    FilterChipHistorial(
                        texto = "Gastos",
                        seleccionado = filtroActual == FiltroMovimiento.GASTOS,
                        dotColor = FinovaError,
                        onClick = { viewModel.cambiarFiltro(FiltroMovimiento.GASTOS) }
                    )
                    FilterChipHistorial(
                        texto = "Ingresos",
                        seleccionado = filtroActual == FiltroMovimiento.INGRESOS,
                        dotColor = FinovaSecondary,
                        onClick = { viewModel.cambiarFiltro(FiltroMovimiento.INGRESOS) }
                    )
                }
            }

            // ---------- Loading / Error ----------
            if (cargando) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = FinovaPrimary)
                    }
                }
            }

            error?.let { msg ->
                item {
                    Surface(
                        color = FinovaErrorContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
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

            // ---------- Vacío ----------
            if (!cargando && grupos.isEmpty() && error == null) {
                item {
                    Text(
                        text = "No hay movimientos para mostrar",
                        style = FinovaBodySm,
                        color = FinovaOnSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
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
                    val mov = grupo.movimientos[index]
                    MovimientoHistorialRow(
                        mov = mov,
                        onClick = { onMovimientoClick(mov.id) }
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

    // ---------- Bottom Sheet ----------
    if (mostrarSheet) {
        NuevaTransaccionSheet(
            onDismiss = { mostrarSheet = false },
            onEscanear = { mostrarSheet = false; onNavigate("escanear") },
            onSubirImagen = { mostrarSheet = false; onNavigate("escanear") },
            onAgregarManual = {
                mostrarSheet = false
                onNavigate("nuevo_movimiento")
            },
            onTransferir = { mostrarSheet = false; onNavigate("transferir") }
        )
    }
}

// ============================================================
// Mapeo DTO → modelo visual
// ============================================================

private fun MovimientoDto.toHistorialVisual(): MovimientoHistorial {
    val esIngreso = tipoMovimiento == "INGRESO"

    val icono: ImageVector = when {
        tipoMovimiento == "TRANSFERENCIA" -> Icons.Outlined.SwapHoriz
        tipoMovimiento == "APORTE_META" -> Icons.Outlined.Savings
        tipoMovimiento == "RETIRO_META" -> Icons.Outlined.AccountBalanceWallet
        categoriaNombre?.contains("Alimenta", ignoreCase = true) == true -> Icons.Outlined.ShoppingCart
        categoriaNombre?.contains("Salud", ignoreCase = true) == true -> Icons.Outlined.Medication
        categoriaNombre?.contains("Transporte", ignoreCase = true) == true -> Icons.Outlined.DirectionsCar
        categoriaNombre?.contains("Servicios", ignoreCase = true) == true -> Icons.Outlined.Bolt
        categoriaNombre?.contains("Salario", ignoreCase = true) == true -> Icons.Outlined.Payments
        esIngreso -> Icons.Outlined.SouthWest
        else -> Icons.Outlined.Receipt
    }

    val hora = extraerHora(fechaMovimiento)

    val subtitulo = buildString {
        if (!categoriaNombre.isNullOrBlank()) append(categoriaNombre)
        if (hora.isNotBlank()) {
            if (isNotEmpty()) append(" · ")
            append(hora)
        }
    }

    return MovimientoHistorial(
        id = id,
        titulo = descripcion ?: "Movimiento",
        subtitulo = subtitulo,
        monto = monto ?: 0.0,
        esIngreso = esIngreso,
        cuenta = cuentaOrigenNombre ?: cuentaDestinoNombre ?: "",
        icono = icono,
        tieneIa = false
    )
}

// Extrae "HH:mm" de "2025-01-15T14:32:00" sin usar java.time
private fun extraerHora(fechaISO: String?): String {
    if (fechaISO.isNullOrBlank()) return ""
    return try {
        val partes = fechaISO.split("T")
        if (partes.size < 2) return ""
        partes[1].take(5)
    } catch (e: Exception) {
        ""
    }
}

// ============================================================
// Agrupar por fecha
// ============================================================

private fun agruparPorFecha(
    visuales: List<MovimientoHistorial>,
    dtos: List<MovimientoDto>
): List<GrupoFecha> {

    val fechaPorId = dtos.associate { it.id to it.fechaMovimiento }

    val map = mutableMapOf<String, MutableList<MovimientoHistorial>>()
    visuales.forEach { mov ->
        val fecha = fechaPorId[mov.id]?.take(10) ?: "Sin fecha"
        map.getOrPut(fecha) { mutableListOf() }.add(mov)
    }

    return map.map { (fecha, lista) ->
        val totalDia = lista.sumOf { if (it.esIngreso) it.monto else -it.monto }
        GrupoFecha(
            fecha = formatearEtiquetaFecha(fecha),
            totalDia = totalDia,
            movimientos = lista
        )
    }.sortedByDescending { it.fecha }
}

// Formatea "2025-01-15" a "HOY · 15 ENE" sin java.time
private fun formatearEtiquetaFecha(fechaISO: String): String {
    return try {
        val partes = fechaISO.split("-")
        if (partes.size < 3) return fechaISO.uppercase()

        val anio = partes[0].toInt()
        val mes = partes[1].toInt()
        val dia = partes[2].toInt()

        val meses = listOf(
            "ENE", "FEB", "MAR", "ABR", "MAY", "JUN",
            "JUL", "AGO", "SEP", "OCT", "NOV", "DIC"
        )
        val mesNombre = if (mes in 1..12) meses[mes - 1] else ""

        // Calcular hoy
        val calHoy = java.util.Calendar.getInstance()
        val anioHoy = calHoy.get(java.util.Calendar.YEAR)
        val mesHoy = calHoy.get(java.util.Calendar.MONTH) + 1
        val diaHoy = calHoy.get(java.util.Calendar.DAY_OF_MONTH)

        val esHoy = anio == anioHoy && mes == mesHoy && dia == diaHoy

        // Calcular ayer
        val calAyer = java.util.Calendar.getInstance()
        calAyer.add(java.util.Calendar.DAY_OF_MONTH, -1)
        val anioAyer = calAyer.get(java.util.Calendar.YEAR)
        val mesAyer = calAyer.get(java.util.Calendar.MONTH) + 1
        val diaAyer = calAyer.get(java.util.Calendar.DAY_OF_MONTH)

        val esAyer = anio == anioAyer && mes == mesAyer && dia == diaAyer

        when {
            esHoy -> "HOY · $dia $mesNombre"
            esAyer -> "AYER · $dia $mesNombre"
            else -> "$dia DE $mesNombre"
        }
    } catch (e: Exception) {
        fechaISO.uppercase()
    }
}

// ============================================================
// Componentes internos
// ============================================================

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