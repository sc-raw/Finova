package com.example.Finova.feature.escaneo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Finova.core.design.theme.*

// ============================================================
// FINOVA — Revisión de Comprobante con IA
// Vertical 4 · feature/escaneo
// Basado en Image 4.html — SOLO VISTA
// ============================================================

@Composable
fun ComprobanteRevisionScreen(
    onBack: () -> Unit,
    onConfirmar: () -> Unit,
    onDescartar: () -> Unit
) {
    var monto by remember { mutableStateOf("45.80") }
    var comercio by remember { mutableStateOf("Metro San Isidro") }
    var nota by remember { mutableStateOf("Compras para la semana (frutas, verduras, lácteos)") }
    var monedaActiva by remember { mutableStateOf("PEN") }
    var itemsExpandidos by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { RevisionTopBar(onBack = onBack) },
        bottomBar = { RevisionBottomBar(onDescartar = onDescartar, onConfirmar = onConfirmar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            // ---------- Chips de estado ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(FinovaTertiaryFixed)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = FinovaTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Luka detectó estos datos",
                        style = FinovaLabelMd,
                        color = FinovaOnTertiaryFixedVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(FinovaSurfaceContainerHigh)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = FinovaPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Confianza 98%",
                        style = FinovaLabelSm,
                        color = FinovaOnSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ---------- Card de análisis IA ----------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(FinovaSurfaceContainerLowest)
                    .padding(14.dp)
            ) {
                // Miniatura del comprobante
                Box(
                    modifier = Modifier
                        .size(width = 70.dp, height = 90.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(FinovaSurfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Receipt,
                        contentDescription = null,
                        tint = FinovaOnSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(FinovaPrimary.copy(alpha = 0.15f))
                            .padding(vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Ver original",
                            style = FinovaLabelSm,
                            color = FinovaPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = FinovaTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Análisis de comprobante",
                            style = FinovaTitleMd,
                            color = FinovaOnSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Identifiqué Supermercados Metro como comercio y S/ 45.80 como monto total. Clasificado en Alimentación por el tipo de productos y tu frecuencia quincenal.",
                        style = FinovaBodySm,
                        color = FinovaOnSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ---------- Monto detectado ----------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(FinovaSurfaceContainerLowest)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MONTO TOTAL DETECTADO",
                        style = FinovaLabelSm,
                        color = FinovaOnSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(FinovaTertiaryFixed)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = FinovaTertiary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = "Sugerido por IA",
                            style = FinovaLabelSm,
                            color = FinovaOnTertiaryFixedVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (monedaActiva == "PEN") "S/" else "$",
                            style = FinovaDisplayLgMobile,
                            color = FinovaPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(4.dp))
                        BasicTextField(
                            value = monto,
                            onValueChange = { monto = it },
                            modifier = Modifier.weight(1f),
                            textStyle = FinovaDisplayLgMobile.copy(
                                color = FinovaOnSurface,
                                fontWeight = FontWeight.Bold
                            ),
                            singleLine = true,
                            cursorBrush = SolidColor(FinovaPrimary)
                        )
                    }
                    // Selector PEN / USD
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(FinovaSurfaceContainerHigh)
                            .padding(4.dp)
                    ) {
                        SelectorMoneda(
                            texto = "PEN S/",
                            activo = monedaActiva == "PEN",
                            onClick = { monedaActiva = "PEN" }
                        )
                        SelectorMoneda(
                            texto = "USD $",
                            activo = monedaActiva == "USD",
                            onClick = { monedaActiva = "USD" }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ---------- Comercio ----------
            CampoRevision(
                label = "Comercio o Proveedor",
                etiquetaEstado = "Sugerido",
                iconoEstado = Icons.Default.AutoAwesome,
                iconoCampo = Icons.Outlined.Storefront,
                iconBg = FinovaSecondaryContainer,
                iconTint = FinovaOnSecondaryContainer,
                valor = comercio,
                onValorChange = { comercio = it },
                trailingIcono = Icons.Default.Edit
            )

            Spacer(Modifier.height(12.dp))

            // ---------- Fecha y hora ----------
            CampoRevision(
                label = "Fecha y Hora de Operación",
                etiquetaEstado = "OCR",
                iconoEstado = Icons.Default.Schedule,
                iconoCampo = Icons.Outlined.CalendarToday,
                iconBg = FinovaSurfaceContainerHigh,
                iconTint = FinovaOnSurfaceVariant,
                valor = "Hoy, 28 Oct 2023 · 14:32",
                onValorChange = { },
                trailingIcono = Icons.Default.EditCalendar
            )

            Spacer(Modifier.height(12.dp))

            // ---------- Categoría ----------
            CampoRevisionCategoria(
                categoria = "Alimentación",
                presupuestoRestante = "Presupuesto mensual: S/ 650.00 restante",
                icono = Icons.Default.ShoppingCart
            )

            Spacer(Modifier.height(12.dp))

            // ---------- Cuenta de pago ----------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(FinovaSurfaceContainerLowest)
                    .clickable { }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(FinovaSurfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalanceWallet,
                        contentDescription = null,
                        tint = FinovaPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "BCP Soles (••4821)",
                            style = FinovaTitleMd,
                            color = FinovaOnSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(FinovaSurfaceContainerHigh)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Débito",
                                style = FinovaLabelSm,
                                color = FinovaOnSurface
                            )
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Saldo disp: S/ 3,420.50",
                        style = FinovaBodySm,
                        color = FinovaOnSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = FinovaOnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            // ---------- Nota ----------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(FinovaSurfaceContainerLowest)
                    .padding(14.dp)
            ) {
                Text(
                    text = "Nota / Detalle adicional",
                    style = FinovaLabelSm,
                    color = FinovaOnSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Outlined.Notes,
                        contentDescription = null,
                        tint = FinovaOnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    BasicTextField(
                        value = nota,
                        onValueChange = { nota = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = FinovaBodyMd.copy(color = FinovaOnSurface),
                        cursorBrush = SolidColor(FinovaPrimary)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ---------- Desglose de ítems ----------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(FinovaSurfaceContainerLowest)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { itemsExpandidos = !itemsExpandidos }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(FinovaTertiaryFixed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ReceiptLong,
                            contentDescription = null,
                            tint = FinovaTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Desglose de ítems (4 productos)",
                            style = FinovaLabelLg,
                            color = FinovaOnSurface
                        )
                        Text(
                            text = "Reconocidos automáticamente por OCR",
                            style = FinovaBodySm,
                            color = FinovaOnSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = if (itemsExpandidos) Icons.Default.ExpandLess
                        else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = FinovaOnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (itemsExpandidos) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ItemRevision("Leche Gloria Evaporada 400g (x2)", "S/ 9.60")
                        ItemRevision("Huevos Pardos La Calera 15un", "S/ 12.80")
                        ItemRevision("Plátano de Seda x 1.2 kg", "S/ 5.40")
                        ItemRevision("Pechuga de Pollo Especial Metro", "S/ 18.00")

                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Subtotal 4 ítems",
                                style = FinovaLabelSm,
                                color = FinovaOnSurfaceVariant
                            )
                            Text(
                                text = "S/ 45.80",
                                style = FinovaLabelLg,
                                color = FinovaOnSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ============================================================
// Componentes internos
// ============================================================

@Composable
private fun RevisionTopBar(onBack: () -> Unit) {
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
                text = "Comprobante",
                style = FinovaTitleMd,
                color = FinovaOnSurface,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Más",
                    tint = FinovaOnSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun RevisionBottomBar(
    onDescartar: () -> Unit,
    onConfirmar: () -> Unit
) {
    Surface(
        color = FinovaSurface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onDescartar,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FinovaSurfaceContainerHigh,
                    contentColor = FinovaOnSurface
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = FinovaOnSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Descartar",
                    style = FinovaLabelLg,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Button(
                onClick = onConfirmar,
                modifier = Modifier
                    .weight(1.5f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FinovaPrimary,
                    contentColor = FinovaOnPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Confirmar y Guardar",
                    style = FinovaLabelLg,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SelectorMoneda(
    texto: String,
    activo: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (activo) FinovaPrimary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = texto,
            style = FinovaLabelSm,
            color = if (activo) FinovaOnPrimary else FinovaOnSurfaceVariant,
            fontWeight = if (activo) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Composable
private fun CampoRevision(
    label: String,
    etiquetaEstado: String,
    iconoEstado: androidx.compose.ui.graphics.vector.ImageVector,
    iconoCampo: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    valor: String,
    onValorChange: (String) -> Unit,
    trailingIcono: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FinovaSurfaceContainerLowest)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = FinovaLabelSm,
                color = FinovaOnSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = iconoEstado,
                    contentDescription = null,
                    tint = FinovaTertiary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = etiquetaEstado,
                    style = FinovaLabelSm,
                    color = FinovaTertiary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconoCampo,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            BasicTextField(
                value = valor,
                onValueChange = onValorChange,
                modifier = Modifier.weight(1f),
                textStyle = FinovaTitleMd.copy(color = FinovaOnSurface),
                singleLine = true,
                cursorBrush = SolidColor(FinovaPrimary)
            )
            Icon(
                imageVector = trailingIcono,
                contentDescription = null,
                tint = FinovaOnSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun CampoRevisionCategoria(
    categoria: String,
    presupuestoRestante: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FinovaSurfaceContainerLowest)
            .clickable { }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(FinovaSecondary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = FinovaOnSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = categoria,
                    style = FinovaTitleMd,
                    color = FinovaOnSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(FinovaSecondaryContainer)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoFixHigh,
                            contentDescription = null,
                            tint = FinovaOnSecondaryContainer,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = "Clasificado",
                            style = FinovaLabelSm,
                            color = FinovaOnSecondaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = presupuestoRestante,
                style = FinovaBodySm,
                color = FinovaOnSurfaceVariant
            )
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
private fun ItemRevision(nombre: String, precio: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(FinovaSurfaceContainerLow)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = nombre,
            style = FinovaBodyMd,
            color = FinovaOnSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = precio,
            style = FinovaLabelLg,
            color = FinovaOnSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}