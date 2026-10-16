package com.example.Finova.feature.estadisticas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.Finova.core.design.theme.*
import com.example.Finova.feature.movimientos.NuevaTransaccionSheet

// ============================================================
// FINOVA — Estadísticas
// Vertical 5 · feature/estadisticas
// ============================================================

private data class BarraMensual(
    val mes: String,
    val ingresos: Float,
    val gastos: Float,
    val esActual: Boolean = false
)

private data class CategoriaDona(
    val nombre: String,
    val monto: Double,
    val porcentaje: Float,
    val color: Color
)

private data class ComparativaCategoria(
    val nombre: String,
    val icono: androidx.compose.ui.graphics.vector.ImageVector,
    val porcentaje: Int,
    val montoTexto: String,
    val esPositivo: Boolean
)

@Composable
fun EstadisticasScreen(
    onNavigate: (String) -> Unit,
    onBack: () -> Unit
) {
    var periodoSeleccionado by remember { mutableStateOf("Mes") }
    var mostrarSheet by remember { mutableStateOf(false) }

    val barras = listOf(
        BarraMensual("Nov", 0.70f, 0.48f),
        BarraMensual("Dic", 0.95f, 0.65f),
        BarraMensual("Ene", 0.72f, 0.46f),
        BarraMensual("Feb", 0.80f, 0.52f),
        BarraMensual("Mar", 0.88f, 0.38f, esActual = true)
    )

    val categorias = listOf(
        CategoriaDona("Alimentación", 620.0, 40f, FinovaPrimary),
        CategoriaDona("Servicios", 340.0, 22f, FinovaSecondary),
        CategoriaDona("Transporte", 280.0, 18f, FinovaTertiary),
        CategoriaDona("Entretenimiento", 210.0, 14f, FinovaPrimaryFixedDim),
        CategoriaDona("Otros", 100.0, 6f, FinovaOutlineVariant)
    )

    val comparativas = listOf(
        ComparativaCategoria("Comida", Icons.Outlined.Restaurant, 4, "+$24 este mes", false),
        ComparativaCategoria("Transporte", Icons.Outlined.DirectionsCar, -15, "-$48 vs Feb", true),
        ComparativaCategoria("Ocio", Icons.Outlined.Movie, -8, "-$18 vs Feb", true)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { EstadisticasTopBar(onBack = onBack) },
        bottomBar = {
            com.example.Finova.core.design.components.BottomNavBar(
                currentRoute = "estadisticas",
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
            // ---------- Selector de periodo ----------
            item {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(FinovaSurfaceContainerLow)
                        .padding(4.dp)
                ) {
                    listOf("Semana", "Mes", "Año").forEach { periodo ->
                        val seleccionado = periodo == periodoSeleccionado
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(
                                    if (seleccionado) FinovaPrimaryContainer
                                    else Color.Transparent
                                )
                                .clickable { periodoSeleccionado = periodo },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = periodo,
                                style = FinovaLabelMd,
                                color = if (seleccionado) FinovaOnPrimary
                                else FinovaOnSurfaceVariant,
                                fontWeight = if (seleccionado) FontWeight.SemiBold
                                else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // ---------- 3 tarjetas de métricas ----------
            item {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        icono = Icons.Outlined.TrendingUp,
                        label = "Ingresos",
                        valor = "+$3,800",
                        sub = "+8.4% vs ant.",
                        iconTint = FinovaSecondary,
                        valorColor = FinovaSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        icono = Icons.Outlined.TrendingDown,
                        label = "Gastos",
                        valor = "-$1,550",
                        sub = "-12.0% ahorro",
                        iconTint = FinovaError,
                        valorColor = FinovaError,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        icono = Icons.Outlined.Savings,
                        label = "Tasa Ahorro",
                        valor = "+59.2%",
                        sub = "Excelente",
                        iconTint = FinovaPrimary,
                        valorColor = FinovaPrimary,
                        badge = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ---------- Gráfico de barras ----------
            item {
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(FinovaSurfaceContainerLowest)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "FLUJO DE CAJA",
                                style = FinovaLabelSm,
                                color = FinovaPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Evolución Mensual",
                                style = FinovaHeadlineSm,
                                color = FinovaOnSurface
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LeyendaPunto(FinovaSecondaryFixed, "Ingreso")
                            Spacer(Modifier.width(8.dp))
                            LeyendaPunto(FinovaPrimaryContainer, "Gasto")
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    GraficoBarras(barras = barras)

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(FinovaSecondaryContainer.copy(alpha = 0.4f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = FinovaSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Gastaste un 12% menos en relación al mes pasado.",
                            style = FinovaLabelMd,
                            color = FinovaOnSecondaryContainer
                        )
                    }
                }
            }

            // ---------- Gráfico de dona ----------
            item {
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(FinovaSurfaceContainerLowest)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CATEGORIZACIÓN",
                                style = FinovaLabelSm,
                                color = FinovaPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Distribución de Gastos",
                                style = FinovaHeadlineSm,
                                color = FinovaOnSurface
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(FinovaSurfaceContainerHigh)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "5 Categorías",
                                style = FinovaLabelSm,
                                color = FinovaOnSurfaceVariant
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GraficoDona(
                            categorias = categorias,
                            totalTexto = "$1,550",
                            modifier = Modifier.size(140.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            categorias.forEach { cat ->
                                FilaCategoriaDona(cat)
                                Spacer(Modifier.height(6.dp))
                            }
                        }
                    }
                }
            }

            // ---------- Comparación vs mes anterior ----------
            item {
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
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
                            text = "Comparación vs Mes Anterior",
                            style = FinovaHeadlineSm,
                            color = FinovaOnSurface
                        )
                        Text(
                            text = "Febrero vs Marzo",
                            style = FinovaLabelSm,
                            color = FinovaOnSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        comparativas.forEach { comp ->
                            ComparativaCard(comp = comp, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // ---------- Botón Descargar reporte ----------
            item {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FinovaPrimaryContainer,
                        contentColor = FinovaOnPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Download,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Descargar reporte",
                        style = FinovaLabelLg,
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
            onAgregarManual = { mostrarSheet = false; onNavigate("detalle_movimiento") },
            onTransferir = { mostrarSheet = false; onNavigate("transferir") }
        )
    }
}

// ============================================================
// Componentes internos
// ============================================================

@Composable
private fun EstadisticasTopBar(onBack: () -> Unit) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "FINOVA",
                    style = FinovaLabelSm,
                    color = FinovaPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Estadísticas",
                    style = FinovaHeadlineSm,
                    color = FinovaOnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
        }
    }
}

@Composable
private fun MetricCard(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    valor: String,
    sub: String,
    iconTint: Color,
    valorColor: Color,
    modifier: Modifier = Modifier,
    badge: Boolean = false
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(FinovaSurfaceContainerLowest)
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = label,
                style = FinovaLabelSm,
                color = FinovaOnSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = valor,
            style = FinovaHeadlineSm,
            color = valorColor,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(2.dp))
        if (badge) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(FinovaPrimary.copy(alpha = 0.1f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = sub,
                    style = FinovaLabelSm,
                    color = FinovaPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            Text(
                text = sub,
                style = FinovaLabelSm,
                color = FinovaOnSurfaceVariant
            )
        }
    }
}

@Composable
private fun LeyendaPunto(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = label,
            style = FinovaLabelSm,
            color = FinovaOnSurfaceVariant
        )
    }
}

@Composable
private fun GraficoBarras(barras: List<BarraMensual>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("$4k", "$2k", "$0").forEach { label ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(FinovaSurfaceContainerHighest)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = label,
                            style = FinovaLabelSm,
                            color = FinovaOnSurfaceVariant
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                barras.forEach { barra ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height(120.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(10.dp)
                                        .fillMaxHeight(barra.ingresos)
                                        .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                        .background(FinovaSecondaryFixed)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(10.dp)
                                        .fillMaxHeight(barra.gastos)
                                        .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                        .background(
                                            if (barra.esActual) FinovaPrimaryContainer
                                            else FinovaPrimaryContainer.copy(alpha = 0.6f)
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            barras.forEach { barra ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (barra.esActual) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(FinovaPrimaryContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Actual",
                                style = FinovaLabelSm,
                                color = FinovaOnPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(2.dp))
                    }
                    Text(
                        text = barra.mes,
                        style = FinovaLabelSm,
                        color = if (barra.esActual) FinovaPrimary
                        else FinovaOnSurfaceVariant,
                        fontWeight = if (barra.esActual) FontWeight.Bold
                        else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun GraficoDona(
    categorias: List<CategoriaDona>,
    totalTexto: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 22f
            val diameter = size.minDimension - stroke
            val topLeft = Offset(
                (size.width - diameter) / 2f,
                (size.height - diameter) / 2f
            )
            val arcSize = Size(diameter, diameter)

            var startAngle = -90f

            categorias.forEach { cat ->
                val sweep = cat.porcentaje / 100f * 360f
                drawArc(
                    color = cat.color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Butt)
                )
                startAngle += sweep
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Total",
                style = FinovaLabelSm,
                color = FinovaOnSurfaceVariant
            )
            Text(
                text = totalTexto,
                style = FinovaHeadlineSm,
                color = FinovaOnSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FilaCategoriaDona(cat: CategoriaDona) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(cat.color)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = cat.nombre,
            style = FinovaBodySm,
            color = FinovaOnSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$%.0f".format(cat.monto),
                style = FinovaLabelSm,
                color = FinovaOnSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "(${cat.porcentaje.toInt()}%)",
                style = FinovaLabelSm,
                color = FinovaOnSurfaceVariant
            )
        }
    }
}

@Composable
private fun ComparativaCard(
    comp: ComparativaCategoria,
    modifier: Modifier = Modifier
) {
    val color = if (comp.esPositivo) FinovaSecondary else FinovaError

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(FinovaSurfaceContainer)
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = comp.icono,
                contentDescription = null,
                tint = FinovaOnSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = comp.nombre,
                style = FinovaLabelSm,
                color = FinovaOnSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${if (comp.porcentaje >= 0) "+" else ""}${comp.porcentaje}%",
                style = FinovaLabelLg,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = if (comp.esPositivo) Icons.Outlined.ArrowDownward
                else Icons.Outlined.ArrowUpward,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = comp.montoTexto,
            style = FinovaLabelSm,
            color = FinovaOnSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}