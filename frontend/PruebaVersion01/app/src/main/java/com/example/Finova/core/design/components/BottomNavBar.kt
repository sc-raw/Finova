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
    FinovaNavItem("dashboard", "Inicio", Icons.Outlined.Home),
    FinovaNavItem("estadisticas", "Estadísticas", Icons.Outlined.BarChart),
    FinovaNavItem("__scan__", "Escanear", Icons.Outlined.ReceiptLong),
    FinovaNavItem("metas", "Metas", Icons.Outlined.Savings),
    FinovaNavItem("perfil", "Configuración", Icons.Outlined.Settings)
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = FinovaSurfaceContainerLowest,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            finovaNavItems.forEach { item ->
                when (item.route) {
                    "__scan__" -> ScanNavItem(
                        item = item,
                        modifier = Modifier.weight(1f),
                        onClick = onScanClick
                    )
                    else -> NavItem(
                        item = item,
                        isActive = currentRoute == item.route,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(item.route) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ScanNavItem(
    item: FinovaNavItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 2.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = FinovaPrimary,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(9.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = "Escanear comprobante",
                tint = Color.White,
                modifier = Modifier.size(21.dp)
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = item.label,
            style = FinovaLabelSm,
            color = FinovaOnSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
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
            .padding(horizontal = 2.dp, vertical = 4.dp)
            .background(
                color = if (isActive) FinovaSecondaryContainer.copy(alpha = 0.45f) else Color.Transparent,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isActive) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = FinovaPrimary,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(9.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = Color.White,
                    modifier = Modifier.size(21.dp)
                )
            }
        } else {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
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
