package com.example.Finova.feature.escaneo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Finova.core.design.theme.*

// ============================================================
// FINOVA — Escanear
// Vertical 4 · feature/escaneo
// Basado en Image 6.html — SOLO VISTA
// ============================================================

@Composable
fun EscanearScreen(
    onBack: () -> Unit,
    onComprobanteCapturado: () -> Unit
) {
    var flashOn by remember { mutableStateOf(false) }
    var tabActiva by remember { mutableStateOf("boleta") }
    var mostrarTips by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1F26)) // Fondo oscuro tipo cámara
    ) {
        // ---------- Fondo de "cámara" (simulado) ----------
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A1F26),
                            Color(0xFF2A3038),
                            Color(0xFF1A1F26)
                        )
                    )
                )
        )

        // ---------- Contenido ----------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp)
        ) {
            // ---------- Top Bar ----------
            EscanearTopBar(
                flashOn = flashOn,
                onFlashToggle = { flashOn = !flashOn },
                onBack = onBack
            )

            Spacer(Modifier.height(12.dp))

            // ---------- Tabs Boleta / Yape ----------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                TabEscanear(
                    texto = "Boleta física",
                    icono = Icons.Outlined.ReceiptLong,
                    activo = tabActiva == "boleta",
                    onClick = { tabActiva = "boleta" }
                )
                Spacer(Modifier.width(10.dp))
                TabEscanear(
                    texto = "Yape / Plin",
                    icono = Icons.Outlined.PhoneAndroid,
                    activo = tabActiva == "yape",
                    onClick = { tabActiva = "yape" }
                )
            }

            Spacer(Modifier.weight(1f))

            // ---------- Marco de escaneo ----------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .aspectRatio(0.75f),
                contentAlignment = Alignment.Center
            ) {
                // Simulación de "boleta" detrás del marco
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF3A4148))
                ) {
                    // Texto simulado de boleta
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "SUPERMERCADO",
                            style = FinovaLabelSm,
                            color = Color(0xFF8A939B),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "PLAZA VEA - MIRAFLORES",
                            style = FinovaLabelSm,
                            color = Color(0xFF8A939B)
                        )
                        Text(
                            text = "RUC: 20600023451",
                            style = FinovaLabelSm,
                            color = Color(0xFF8A939B)
                        )
                        Spacer(Modifier.height(8.dp))
                        repeat(6) { i ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "PRODUCTO ${i + 1}",
                                    style = FinovaLabelSm,
                                    color = Color(0xFF6A737B)
                                )
                                Text(
                                    text = "S/ ${(10 + i * 3)}.00",
                                    style = FinovaLabelSm,
                                    color = Color(0xFF6A737B)
                                )
                            }
                        }
                    }
                }

                // Marco guía con esquinas teal
                MarcoEscaner()

                // Chip "IA activa"
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 8.dp, y = 8.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(FinovaTertiaryContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = FinovaOnTertiaryContainer,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "¡IA activa!",
                            style = FinovaLabelSm,
                            color = FinovaOnTertiaryContainer,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Chip "Detectando total..."
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-8).dp, y = (-8).dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xCC1A1F26))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(FinovaPrimaryFixed)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Detectando total...",
                            style = FinovaLabelSm,
                            color = FinovaInverseOnSurface
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // ---------- Texto de ayuda ----------
            Text(
                text = "Apunta a la boleta, recibo o comprobante. Luka extraerá montos y fecha automáticamente.",
                style = FinovaBodyMd,
                color = FinovaInverseOnSurface.copy(alpha = 0.9f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(24.dp))

            // ---------- Controles inferiores ----------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Galería
                ControlInferior(
                    icono = Icons.Outlined.PhotoLibrary,
                    label = "Galería",
                    onClick = onComprobanteCapturado
                )

                // Shutter (botón central grande)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(FinovaPrimaryContainer)
                        .clickable(onClick = onComprobanteCapturado),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(FinovaSurfaceContainerLowest),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(FinovaPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Capturar",
                                tint = FinovaOnPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // Ayuda
                ControlInferior(
                    icono = Icons.Outlined.Help,
                    label = "Ayuda",
                    onClick = { mostrarTips = true }
                )
            }

            Spacer(Modifier.height(16.dp))

            // ---------- Footer con notas ----------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = FinovaPrimaryFixed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Multi-moneda S/ y $",
                        style = FinovaLabelSm,
                        color = FinovaInverseOnSurface.copy(alpha = 0.6f)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = FinovaSecondaryFixed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Cifrado seguro",
                        style = FinovaLabelSm,
                        color = FinovaInverseOnSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    // ---------- Modal de tips ----------
    if (mostrarTips) {
        AlertDialog(
            onDismissRequest = { mostrarTips = false },
            confirmButton = {
                Button(
                    onClick = { mostrarTips = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FinovaPrimary,
                        contentColor = FinovaOnPrimary
                    )
                ) {
                    Text("Entendido", style = FinovaLabelLg)
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.TipsAndUpdates,
                    contentDescription = null,
                    tint = FinovaPrimary,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Tips de escaneo",
                    style = FinovaHeadlineSm,
                    color = FinovaOnSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TipItem("Asegura buena iluminación y evita sombras directas sobre los números.")
                    TipItem("Mantén el comprobante estirado dentro del recuadro teal.")
                    TipItem("Para Yape o Plin, puedes cargar la captura de pantalla desde galería.")
                }
            }
        )
    }
}

// ============================================================
// Componentes internos
// ============================================================

@Composable
private fun EscanearTopBar(
    flashOn: Boolean,
    onFlashToggle: () -> Unit,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0x991A1F26))
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = FinovaInverseOnSurface,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "Escanear",
                    style = FinovaHeadlineSm,
                    color = FinovaInverseOnSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(FinovaSecondaryFixed)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "CÁMARA INTELIGENTE",
                        style = FinovaLabelSm,
                        color = FinovaPrimaryFixedDim,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0x991A1F26))
                    .clickable(onClick = onFlashToggle),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (flashOn) Icons.Default.FlashOn
                    else Icons.Default.FlashOff,
                    contentDescription = "Flash",
                    tint = FinovaInverseOnSurface,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0x991A1F26)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Grid4x4,
                    contentDescription = "Cuadrícula",
                    tint = FinovaInverseOnSurface,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun TabEscanear(
    texto: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    activo: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(
                if (activo) FinovaSecondary
                else Color(0xB31A1F26)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = if (activo) FinovaOnSecondary else FinovaInverseOnSurface,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = texto,
            style = FinovaLabelMd,
            color = if (activo) FinovaOnSecondary else FinovaInverseOnSurface
        )
    }
}

@Composable
private fun MarcoEscaner() {
    val colorMarco = FinovaPrimaryFixed
    val grosor = 5.dp
    val largo = 36.dp

    Box(modifier = Modifier.fillMaxSize()) {
        // Esquina superior izquierda
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(largo)
                .border(
                    width = grosor,
                    color = colorMarco,
                    shape = RoundedCornerShape(topStart = 16.dp)
                )
        )
        // Esquina superior derecha
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(largo)
                .border(
                    width = grosor,
                    color = colorMarco,
                    shape = RoundedCornerShape(topEnd = 16.dp)
                )
        )
        // Esquina inferior izquierda
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(largo)
                .border(
                    width = grosor,
                    color = colorMarco,
                    shape = RoundedCornerShape(bottomStart = 16.dp)
                )
        )
        // Esquina inferior derecha
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(largo)
                .border(
                    width = grosor,
                    color = colorMarco,
                    shape = RoundedCornerShape(bottomEnd = 16.dp)
                )
        )
    }
}

@Composable
private fun ControlInferior(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0x991A1F26)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = label,
                tint = FinovaInverseOnSurface,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = FinovaLabelSm,
            color = FinovaInverseOnSurface.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun TipItem(texto: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Default.Lightbulb,
            contentDescription = null,
            tint = FinovaPrimary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = texto,
            style = FinovaBodyMd,
            color = FinovaOnSurfaceVariant
        )
    }
}