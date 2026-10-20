package com.example.Finova.feature.estadisticas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
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
import com.example.Finova.core.design.theme.*
import com.example.Finova.feature.movimientos.NuevaTransaccionSheet

// ============================================================
// FINOVA — Metas de Ahorro
// Vertical 5 · feature/estadisticas
// ============================================================

private data class MetaVisual(
    val titulo: String,
    val descripcion: String,
    val actual: Double,
    val objetivo: Double,
    val porcentaje: Int,
    val estadoTexto: String,
    val estadoColor: Color,
    val footerLabel: String,
    val footerValor: String,
    val icono: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val progressColor: Color
)

@Composable
fun MetasScreen(
    onNavigate: (String) -> Unit,
    onAnadirFondos: () -> Unit,
    onBack: () -> Unit
) {
    var mostrarSheet by remember { mutableStateOf(false) }

    val metas = listOf(
        MetaVisual(
            titulo = "Fondo de emergencia",
            descripcion = "Protección financiera familiar",
            actual = 2400.00,
            objetivo = 3000.00,
            porcentaje = 80,
            estadoTexto = "Meta casi lista",
            estadoColor = FinovaPrimary,
            footerLabel = "Te faltan",
            footerValor = "$600.00 · Proyección: 1 mes",
            icono = Icons.Outlined.Shield,
            iconBg = FinovaSecondaryContainer,
            iconTint = FinovaSecondary,
            progressColor = FinovaPrimary
        ),
        MetaVisual(
            titulo = "Laptop nueva para trabajo",
            descripcion = "Herramienta de desarrollo",
            actual = 650.00,
            objetivo = 1200.00,
            porcentaje = 54,
            estadoTexto = "En curso",
            estadoColor = FinovaTertiary,
            footerLabel = "Aporte programado",
            footerValor = "$150 quincenal",
            icono = Icons.Outlined.LaptopMac,
            iconBg = FinovaTertiaryFixed,
            iconTint = FinovaTertiary,
            progressColor = FinovaTertiary
        ),
        MetaVisual(
            titulo = "Viaje a la playa",
            descripcion = "Vacaciones de fin de año",
            actual = 600.00,
            objetivo = 2000.00,
            porcentaje = 30,
            estadoTexto = "Iniciando",
            estadoColor = FinovaSecondary,
            footerLabel = "Fecha límite estimada",
            footerValor = "Diciembre 2025",
            icono = Icons.Outlined.BeachAccess,
            iconBg = FinovaSecondaryFixed,
            iconTint = FinovaSecondary,
            progressColor = FinovaSecondary
        )
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { MetasTopBar(onBack = onBack) },
        bottomBar = {
            com.example.Finova.core.design.components.BottomNavBar(
                currentRoute = "metas",
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
            // ---------- Encabezado ----------
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Metas de Ahorro",
                            style = FinovaHeadlineMd,
                            color = FinovaOnSurface
                        )
                        Spacer(Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(FinovaPrimary)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "3 metas activas",
                                style = FinovaBodySm,
                                color = FinovaOnSurfaceVariant
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(FinovaSurfaceContainerHigh)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.TrendingUp,
                            contentDescription = null,
                            tint = FinovaPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "+18.5% este mes",
                            style = FinovaLabelSm,
                            color = FinovaPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // ---------- Subtítulo "Tus Planes Activos" ----------
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tus Planes Activos",
                        style = FinovaLabelLg,
                        color = FinovaOnSurface
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Prioridad",
                            style = FinovaLabelSm,
                            color = FinovaPrimary
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Outlined.SwapVert,
                            contentDescription = null,
                            tint = FinovaPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // ---------- Tarjetas de metas ----------
            items(metas.size) { index ->
                MetaCard(
                    meta = metas[index],
                    onAnadirFondos = onAnadirFondos
                )
                Spacer(Modifier.height(12.dp))
            }

            // ---------- Botón crear meta ----------
            item {
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FinovaPrimary,
                        contentColor = FinovaOnPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Crear nueva meta",
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
            onAgregarManual = {
                mostrarSheet = false
                onNavigate("nuevo_movimiento")
            },
            onTransferir = { mostrarSheet = false; onNavigate("transferir") }
        )
    }
}

// ============================================================
// Componentes internos
// ============================================================

@Composable
private fun MetasTopBar(onBack: () -> Unit) {
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
                    text = "Metas",
                    style = FinovaHeadlineSm,
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
        }
    }
}

@Composable
private fun MetaCard(
    meta: MetaVisual,
    onAnadirFondos: () -> Unit
) {
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
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(meta.iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = meta.icono,
                    contentDescription = null,
                    tint = meta.iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meta.titulo,
                    style = FinovaHeadlineSm,
                    color = FinovaOnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = meta.descripcion,
                    style = FinovaBodySm,
                    color = FinovaOnSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(meta.iconBg)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = meta.iconTint,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${meta.porcentaje}%",
                        style = FinovaLabelSm,
                        color = meta.iconTint,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$%.2f".format(meta.actual),
                    style = FinovaHeadlineLg,
                    color = FinovaOnSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "/ $%.2f".format(meta.objetivo),
                    style = FinovaBodySm,
                    color = FinovaOnSurfaceVariant
                )
            }
            Text(
                text = meta.estadoTexto,
                style = FinovaLabelSm,
                color = meta.estadoColor,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(FinovaSurfaceContainer)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(meta.porcentaje / 100f)
                    .clip(RoundedCornerShape(999.dp))
                    .background(meta.progressColor)
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(FinovaSurfaceContainerLow)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${meta.footerLabel}: ${meta.footerValor}",
                style = FinovaLabelSm,
                color = FinovaOnSurfaceVariant
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(FinovaSurfaceContainerHigh)
                    .clickable(onClick = onAnadirFondos)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = FinovaPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Añadir",
                    style = FinovaLabelSm,
                    color = FinovaPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}