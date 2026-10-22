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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Finova.core.design.theme.*

import android.net.Uri
import android.content.Context
import androidx.core.content.FileProvider
import androidx.compose.ui.platform.LocalContext
import java.io.File
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

// ============================================================
// FINOVA — Escanear
// Vertical 4 · feature/escaneo
// Basado en Image 6.html — SOLO VISTA
// ============================================================

@Composable
fun EscanearScreen(
    onBack: () -> Unit,
    onComprobanteCapturado: (Uri) -> Unit,
    onImagenSeleccionada: (Uri) -> Unit
) {
    val context = LocalContext.current
    var uriCapturaPendiente by remember { mutableStateOf<Uri?>(null) }
    var camaraLanzada by remember { mutableStateOf(false) }

    val capturarFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { guardada ->
        val uri = uriCapturaPendiente
        if (guardada && uri != null) {
            onComprobanteCapturado(uri)
        } else {
            // Si el usuario cancela la cámara, volver a la pantalla anterior.
            onBack()
        }
        uriCapturaPendiente = null
    }

    // Mantener una pantalla negra vacía mientras Android abre la cámara real.
    // No renderizar la interfaz falsa de escaneo en ningún momento.
    LaunchedEffect(Unit) {
        if (!camaraLanzada) {
            camaraLanzada = true
            val uri = crearUriTemporalCaptura(context)
            uriCapturaPendiente = uri
            capturarFoto.launch(uri)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    )
}

// Crea una URI segura para que la aplicación de cámara guarde la fotografía.
private fun crearUriTemporalCaptura(context: Context): Uri {
    val carpeta = File(context.cacheDir, "comprobantes").apply { mkdirs() }
    val archivo = File.createTempFile("comprobante_", ".jpg", carpeta)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        archivo
    )
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