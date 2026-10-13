package com.example.Finova.feature.perfil

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Finova.core.design.components.*
import com.example.Finova.core.design.theme.*

// ============================================================
// FINOVA — Perfil
// Vertical 1 · feature/perfil
// Basado en Image 2.html — SOLO VISTA
// ============================================================

@Composable
fun PerfilScreen(
    onNavigate: (String) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { PerfilTopBar() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // ---------- Grupo: Mi Cuenta ----------
            PerfilSeccionTitulo("Mi Cuenta")
            PerfilCard {
                PerfilItem(
                    icono = Icons.Outlined.Badge,
                    titulo = "Mis datos",
                    subtitulo = "Información personal y seguridad",
                    onClick = { onNavigate("mis_datos") }
                )
            }

            Spacer(Modifier.height(20.dp))

            // ---------- Grupo: Preferencias ----------
            PerfilSeccionTitulo("Preferencias")
            PerfilCard {
                PerfilItem(
                    icono = Icons.Outlined.Paid,
                    titulo = "Moneda principal",
                    subtitulo = "PEN - Sol Peruano (S/)",
                    onClick = { }
                )
            }

            Spacer(Modifier.height(20.dp))

            // ---------- Grupo: Aplicación ----------
            PerfilSeccionTitulo("Aplicación")
            PerfilCard {
                PerfilItem(
                    icono = Icons.Outlined.Description,
                    titulo = "Términos y condiciones",
                    subtitulo = "Uso de plataforma y licencias",
                    trailingIcono = Icons.Outlined.OpenInNew,
                    onClick = { }
                )
                PerfilItem(
                    icono = Icons.Outlined.Shield,
                    titulo = "Política de privacidad y datos OCR",
                    subtitulo = "Tus datos nunca se comparten",
                    subtituloColor = FinovaPrimary,
                    onClick = { }
                )
                PerfilItem(
                    icono = Icons.Outlined.Info,
                    titulo = "Acerca de Finova",
                    subtitulo = "Versión 1.0.0 (Android Build 104)",
                    trailingChip = "Oficial",
                    onClick = { }
                )
            }

            Spacer(Modifier.height(24.dp))

            // ---------- Botón Cerrar Sesión ----------
            Button(
                onClick = { onNavigate("login") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FinovaErrorContainer.copy(alpha = 0.6f),
                    contentColor = FinovaError
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Cerrar sesión",
                    style = FinovaLabelLg,
                    fontWeight = FontWeight.SemiBold
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
private fun PerfilTopBar() {
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
                        .background(FinovaPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "F",
                        style = FinovaHeadlineSm,
                        color = FinovaOnPrimary
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = "FINOVA",
                        style = FinovaLabelSm,
                        color = FinovaPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Mi Perfil",
                        style = FinovaHeadlineSm,
                        color = FinovaOnSurface
                    )
                }
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
private fun PerfilSeccionTitulo(texto: String) {
    Text(
        text = texto.uppercase(),
        style = FinovaLabelMd,
        color = FinovaOnSurfaceVariant,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
    )
}

@Composable
private fun PerfilCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FinovaSurfaceContainerLowest)
            .padding(8.dp),
        content = content
    )
}

@Composable
private fun PerfilItem(
    icono: ImageVector,
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit,
    subtituloColor: Color = FinovaOnSurfaceVariant,
    trailingIcono: ImageVector = Icons.Default.ChevronRight,
    trailingChip: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(FinovaSurfaceContainerLow),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = FinovaPrimary,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                style = FinovaLabelLg,
                color = FinovaOnSurface
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitulo,
                style = FinovaBodySm,
                color = subtituloColor
            )
        }
        if (trailingChip != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(FinovaSurfaceContainer)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = trailingChip,
                    style = FinovaLabelSm,
                    color = FinovaOnSurfaceVariant
                )
            }
        } else {
            Icon(
                imageVector = trailingIcono,
                contentDescription = null,
                tint = FinovaOutlineVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}