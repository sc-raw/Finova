package com.example.Finova.core.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.Finova.core.design.theme.*

// ============================================================
// FINOVA — Bottom Navigation Bar con FAB central
// Estructura: Column con Spacer arriba (para el FAB que sobresale)
// y Box con la barra + FAB abajo.
// ============================================================

data class FinovaNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val finovaNavItems = listOf(
    FinovaNavItem("dashboard",    "Inicio",       Icons.Outlined.Home),
    FinovaNavItem("estadisticas", "Estadísticas", Icons.Outlined.BarChart),
    FinovaNavItem("__fab__",      "",             Icons.Filled.QrCodeScanner),
    FinovaNavItem("estadisticas", "Metas",        Icons.Outlined.Savings),
    FinovaNavItem("perfil",       "Perfil",       Icons.Outlined.Person)
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val barHeight = 72.dp
    val fabSize = 56.dp
    val fabOverhang = fabSize / 2   // 28dp — cuánto sobresale el FAB

    Column(modifier = modifier.fillMaxWidth()) {

        // -------- Espacio reservado arriba para el FAB que sobresale --------
        Spacer(Modifier.height(fabOverhang))

        // -------- Barra + FAB --------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
        ) {
            // Barra blanca (fondo completo del Box)
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = FinovaSurfaceContainerLowest,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    finovaNavItems.forEach { item ->
                        if (item.route == "__fab__") {
                            Spacer(modifier = Modifier.weight(1f))
                        } else {
                            NavItem(
                                item = item,
                                isActive = currentRoute == item.route,
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigate(item.route) }
                            )
                        }
                    }
                }
            }

            // FAB — pegado al borde SUPERIOR de la barra, sobresaliendo
            // exactamente la mitad de su altura (28dp)
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = -fabOverhang)  // sube 28dp → queda mitad dentro, mitad fuera
                    .size(fabSize),
                shape = CircleShape,
                color = FinovaAccentLightTeal,
                shadowElevation = 10.dp,
                onClick = onScanClick
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.QrCodeScanner,
                        contentDescription = "Escanear comprobante",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NavItem(
    item: FinovaNavItem,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val color = if (isActive) FinovaPrimary else FinovaOnSurfaceVariant

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = item.label,
            style = FinovaLabelSm,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}