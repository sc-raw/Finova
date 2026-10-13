package com.example.Finova.feature.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Finova.core.design.theme.*

// ============================================================
// FINOVA — Mis Datos
// Vertical 1 · feature/perfil
// Basado en Image 4.html — SOLO VISTA
// ============================================================

@Composable
fun MisDatosScreen(
    onBack: () -> Unit
) {
    var nombre by remember { mutableStateOf("Andrea Morales") }
    var correo by remember { mutableStateOf("andrea.morales@correo.com") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { MisDatosTopBar(onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            // ---------- Header "Mis Datos" + Cuenta verificada ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(FinovaSurfaceContainer)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = FinovaOnSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Mis Datos",
                        style = FinovaHeadlineSm,
                        color = FinovaOnSurface
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = FinovaPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Cuenta Verificada Perú",
                            style = FinovaLabelSm,
                            color = FinovaPrimary
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(FinovaSurfaceContainerLow),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.HelpOutline,
                        contentDescription = "Ayuda",
                        tint = FinovaOnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ---------- Avatar + Cambiar foto ----------
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.size(112.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(FinovaSurfaceContainerHighest),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = FinovaOnSurfaceVariant,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(FinovaPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = FinovaOnPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(FinovaSurfaceContainerHigh)
                        .clickable { }
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = FinovaPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Cambiar foto",
                        style = FinovaLabelMd,
                        color = FinovaPrimary
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Formatos permitidos: JPG, PNG (Máx. 5 MB)",
                    style = FinovaBodySm,
                    color = FinovaOnSurfaceVariant
                )
            }

            Spacer(Modifier.height(20.dp))

            // ---------- Banner de seguridad ----------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(FinovaSecondaryContainer)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(FinovaSurfaceContainerLowest),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = FinovaSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Protección Biométrica y DNI",
                        style = FinovaLabelMd,
                        color = FinovaOnSecondaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Tus datos fiscales y personales están cifrados de extremo a extremo.",
                        style = FinovaBodySm,
                        color = FinovaOnSecondaryContainer
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ---------- Card: Información Personal ----------
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
                        text = "Información Personal",
                        style = FinovaLabelLg,
                        color = FinovaOnSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(FinovaSurfaceContainerLow)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Finova ID: #PE-8294",
                            style = FinovaLabelSm,
                            color = FinovaOnSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // --- Nombre ---
                CampoMisDatos(
                    label = "Nombre completo",
                    valor = nombre,
                    onValorChange = { nombre = it },
                    icono = Icons.Outlined.Person,
                    trailingIcono = Icons.Outlined.Edit
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Tal como figura en tu Documento Nacional de Identidad (DNI).",
                    style = FinovaLabelSm,
                    color = FinovaOnSurfaceVariant
                )

                Spacer(Modifier.height(14.dp))

                // --- Correo ---
                CampoMisDatos(
                    label = "Correo electrónico",
                    valor = correo,
                    onValorChange = { correo = it },
                    icono = Icons.Outlined.Mail,
                    keyboardType = KeyboardType.Email,
                    trailingCheck = true
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Recibirás los resúmenes mensuales y comprobantes aquí.",
                    style = FinovaLabelSm,
                    color = FinovaOnSurfaceVariant
                )

                Spacer(Modifier.height(14.dp))

                // --- Contraseña ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Contraseña",
                        style = FinovaLabelSm,
                        color = FinovaOnSurfaceVariant
                    )
                    Text(
                        text = "Actualizada hace 2 meses",
                        style = FinovaLabelSm,
                        color = FinovaOnSurfaceVariant
                    )
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(FinovaSurfaceContainerLow)
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = FinovaPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "••••••••••••",
                        style = FinovaBodyMd,
                        color = FinovaOnSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(FinovaSurfaceContainerHigh)
                            .clickable { }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Cambiar",
                            style = FinovaLabelMd,
                            color = FinovaPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // --- DNI / RUC ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(FinovaSurfaceContainerLow)
                        .clickable { }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(FinovaPrimaryContainer.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Badge,
                            contentDescription = null,
                            tint = FinovaPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DNI / RUC Asociado",
                            style = FinovaLabelMd,
                            color = FinovaOnSurface,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "7482**** • Facturación SUNAT activa",
                            style = FinovaBodySm,
                            color = FinovaOnSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = FinovaOnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ---------- Botones ----------
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FinovaPrimaryContainer,
                    contentColor = FinovaOnPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Guardar datos",
                    style = FinovaLabelLg,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FinovaSurfaceContainerHigh,
                    contentColor = FinovaOnSurface
                )
            ) {
                Text(
                    text = "Cancelar",
                    style = FinovaLabelLg,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(16.dp))

            // ---------- Footer legal ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = FinovaOnSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Finova protege tu privacidad bajo la Ley N° 29733 de Perú.",
                    style = FinovaBodySm,
                    color = FinovaOnSurfaceVariant
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ============================================================
// Componentes internos
// ============================================================

@Composable
private fun MisDatosTopBar(onBack: () -> Unit) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Finova",
                    style = FinovaLabelMd,
                    color = FinovaOnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CampoMisDatos(
    label: String,
    valor: String,
    onValorChange: (String) -> Unit,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcono: androidx.compose.ui.graphics.vector.ImageVector? = null,
    trailingCheck: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = FinovaLabelSm,
            color = FinovaOnSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(FinovaSurfaceContainerLow)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = FinovaPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            BasicTextField(
                value = valor,
                onValueChange = onValorChange,
                modifier = Modifier.weight(1f),
                textStyle = FinovaBodyMd.copy(color = FinovaOnSurface),
                singleLine = true,
                cursorBrush = SolidColor(FinovaPrimary),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
            )
            if (trailingIcono != null) {
                Icon(
                    imageVector = trailingIcono,
                    contentDescription = null,
                    tint = FinovaOnSurfaceVariant,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { }
                )
            }
            if (trailingCheck) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Verificado",
                    tint = FinovaSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}