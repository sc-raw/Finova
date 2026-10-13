package com.example.Finova.feature.cuentas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.Finova.core.design.components.*
import com.example.Finova.core.design.theme.*
import com.example.Finova.core.util.formatSoles

// ============================================================
// FINOVA — Mis Cuentas
// Vertical 2 · feature/cuentas
// Basado en Image 4.html — SOLO VISTA (datos hardcodeados)
// ============================================================

private data class CuentaVisual(
    val titulo: String,
    val numero: String,
    val estado: String,
    val saldo: Double,
    val icono: ImageVector,
    val iconBg: Color,
    val iconTint: Color
)

@Composable
fun MisCuentasScreen(
    onNavigate: (String) -> Unit,
    onTransferClick: () -> Unit
) {
    // --- Datos hardcodeados (temporal) ---
    val cuentas = listOf(
        CuentaVisual(
            titulo = "BCP Soles - Cuenta Sueldo",
            numero = "•••• 4821",
            estado = "Hoy",
            saldo = 5820.00,
            icono = Icons.Outlined.AccountBalance,
            iconBg = FinovaSecondaryContainer.copy(alpha = 0.6f),
            iconTint = FinovaSecondary
        ),
        CuentaVisual(
            titulo = "BCP Soles - Ahorros",
            numero = "•••• 9023",
            estado = "Soles",
            saldo = 2437.50,
            icono = Icons.Outlined.Savings,
            iconBg = FinovaPrimaryContainer.copy(alpha = 0.2f),
            iconTint = FinovaPrimary
        ),
        CuentaVisual(
            titulo = "BBVA - Tarjeta Débito",
            numero = "•••• 1145",
            estado = "Activa",
            saldo = 1200.00,
            icono = Icons.Outlined.CreditCard,
            iconBg = FinovaTertiaryFixed.copy(alpha = 0.4f),
            iconTint = FinovaTertiary
        )
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { CuentasTopBar() },
        bottomBar = {
            BottomNavBar(
                currentRoute = "mis_cuentas",
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
            // ---------- Título de sección + Filtrar ----------
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cuentas (${cuentas.size})",
                        style = FinovaHeadlineSm,
                        color = FinovaOnSurface
                    )
                    FinovaTextButton(
                        text = "Filtrar",
                        onClick = { },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = FinovaPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }

            // ---------- Lista de cuentas ----------
            items(cuentas.size) { index ->
                CuentaCard(cuenta = cuentas[index])
                Spacer(Modifier.height(12.dp))
            }

            // ---------- Botones duales ----------
            item {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Nueva cuenta (outline)
                    Button(
                        onClick = { },
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

                    // Transferir (primary)
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
}

// ============================================================
// Componentes internos
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
            Row(verticalAlignment = Alignment.CenterVertically) {
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
}

@Composable
private fun CuentaCard(cuenta: CuentaVisual) {
    FinovaCard(
        modifier = Modifier.padding(horizontal = 16.dp),
        padding = 16.dp
    ) {
        // Fila superior: icono + título + chip "Bancaria"
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

            // Chip "Bancaria"
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

        // Fila inferior: "Saldo disponible" + monto
        Column {
            Text(
                text = "Saldo disponible",
                style = FinovaLabelSm,
                color = FinovaOnSurfaceVariant
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = formatSoles(cuenta.saldo),
                style = FinovaHeadlineMd,
                color = FinovaOnSurface
            )
        }
    }
}