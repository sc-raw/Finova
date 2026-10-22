package com.example.Finova.feature.estadisticas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.Finova.core.design.theme.*
import com.example.Finova.core.network.ApiClient
import com.example.Finova.core.network.dto.CuentaDto
import com.example.Finova.core.network.dto.MetaDto
import com.example.Finova.core.util.SessionManager
import kotlinx.coroutines.launch

@Composable
fun AnadirFondosScreen(
    metaId: Long,
    onBack: () -> Unit,
    onConfirmar: () -> Unit
) {
    val context = LocalContext.current
    val usuarioId = remember { SessionManager(context).obtenerUsuarioId() }
    val scope = rememberCoroutineScope()
    var meta by remember { mutableStateOf<MetaDto?>(null) }
    var cuentas by remember { mutableStateOf<List<CuentaDto>>(emptyList()) }
    var cuentaSeleccionada by remember { mutableStateOf<CuentaDto?>(null) }
    var mostrarCuentas by remember { mutableStateOf(false) }
    var monto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(true) }
    var enviando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var mostrarExito by remember { mutableStateOf(false) }
    var aporteCompletoMeta by remember { mutableStateOf(false) }
    var mostrarAvisoMetaCompletada by remember { mutableStateOf(false) }

    LaunchedEffect(metaId, usuarioId) {
        cargando = true
        error = null
        try {
            if (usuarioId == -1L) error = "No hay una sesión activa."
            else {
                meta = ApiClient.apiService.obtenerMeta(metaId)
                val monedaMeta = (meta?.moneda ?: "PEN").uppercase()
                cuentas = ApiClient.apiService.listarCuentasPorUsuario(usuarioId)
                    .filter { (it.saldoActual ?: 0.0) > 0.0 && (it.moneda ?: "PEN").uppercase() == monedaMeta }
                cuentaSeleccionada = cuentas.firstOrNull()
                if (cuentas.isEmpty()) error = "No tienes cuentas con saldo disponible en ${if (monedaMeta == "USD") "dólares" else "soles"} para realizar el aporte."
            }
        } catch (e: Exception) {
            error = e.message ?: "No se pudieron cargar los datos."
        } finally { cargando = false }
    }

    val monedaMeta = (meta?.moneda ?: "PEN").uppercase()
    val simbolo = if (monedaMeta == "USD") "US$" else "S/"
    val montoNumerico = monto.replace(',', '.').toDoubleOrNull() ?: 0.0
    val saldoDisponible = cuentaSeleccionada?.saldoActual ?: 0.0
    val montoActualMeta = meta?.montoActual ?: 0.0
    val montoObjetivoMeta = meta?.montoObjetivo ?: 0.0
    val montoPendiente = (montoObjetivoMeta - montoActualMeta).coerceAtLeast(0.0)
    val metaCompletada = meta != null && montoPendiente <= 0.0
    val puedeConfirmar = !cargando && !enviando && cuentaSeleccionada != null &&
            montoNumerico > 0.0 && montoNumerico <= saldoDisponible &&
            montoNumerico <= montoPendiente && meta != null && !metaCompletada

    LaunchedEffect(metaCompletada) {
        if (metaCompletada) mostrarAvisoMetaCompletada = true
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { AnadirFondosTopBar(onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(28.dp).clip(CircleShape).background(FinovaSecondaryContainer), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Savings, null, tint = FinovaOnSecondaryContainer, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("Aporte a tus objetivos", style = FinovaLabelLg, color = FinovaPrimaryContainer)
                }
                Row(Modifier.clip(RoundedCornerShape(999.dp)).background(FinovaSurfaceContainer).padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Lock, null, tint = FinovaPrimary, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Movimiento seguro", style = FinovaLabelSm, color = FinovaOnSurfaceVariant)
                }
            }

            Spacer(Modifier.height(20.dp))
            if (cargando) {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = FinovaPrimary) }
            } else {
                Text("Selecciona tu cuenta de origen", style = FinovaLabelLg, color = FinovaOnSurface)
                Spacer(Modifier.height(6.dp))
                Box {
                    CuentaSelectorCard(
                        titulo = cuentaSeleccionada?.nombre ?: "Seleccionar cuenta",
                        numero = cuentaSeleccionada?.numeroCuenta?.let { "•••• ${it.takeLast(4)}" } ?: "Sin cuenta",
                        saldo = "Saldo disponible: $simbolo %.2f".format(saldoDisponible),
                        onClick = { mostrarCuentas = true }
                    )
                    DropdownMenu(expanded = mostrarCuentas, onDismissRequest = { mostrarCuentas = false }) {
                        cuentas.forEach { cuenta ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(cuenta.nombre ?: cuenta.bancoNombre ?: "Cuenta", fontWeight = FontWeight.SemiBold)
                                        Text("Saldo: $simbolo %.2f".format(cuenta.saldoActual ?: 0.0), style = FinovaBodySm)
                                    }
                                },
                                onClick = { cuentaSeleccionada = cuenta; mostrarCuentas = false }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text("Meta destino", style = FinovaLabelLg, color = FinovaOnSurface)
                Spacer(Modifier.height(6.dp))
                MetaSelectorCard(
                    nombre = meta?.nombre ?: "Cargando meta...",
                    monto = "$simbolo %.2f acumulado de $simbolo %.2f".format(meta?.montoActual ?: 0.0, meta?.montoObjetivo ?: 0.0),
                    porcentaje = if ((meta?.montoObjetivo ?: 0.0) > 0.0) (((meta?.montoActual ?: 0.0) / (meta?.montoObjetivo ?: 1.0)) * 100).toInt().coerceIn(0, 100) else 0
                )

                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Monto a ingresar", style = FinovaLabelLg, color = FinovaOnSurface)
                    Text("Pendiente: $simbolo %.2f".format(montoPendiente), style = FinovaLabelSm, color = FinovaPrimaryContainer, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = monto,
                    onValueChange = { nuevo -> if (nuevo.isEmpty() || nuevo.matches(Regex("^\\d*[.,]?\\d{0,2}$"))) monto = nuevo },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Text(simbolo, style = FinovaHeadlineLg, color = FinovaPrimaryContainer) },
                    placeholder = { Text("0.00") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Rápido:", style = FinovaLabelSm, color = FinovaOutline)
                    listOf(50.0, 100.0, 200.0).forEach { extra ->
                        SuggestionChip(
                            onClick = { monto = "%.2f".format((montoNumerico + extra).coerceAtMost(montoPendiente)) },
                            enabled = !metaCompletada && montoPendiente > 0.0,
                            label = { Text("+S/ ${extra.toInt()}") }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { monto = "%.2f".format(montoPendiente) },
                    enabled = !metaCompletada && montoPendiente > 0.0,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Outlined.Savings, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Completar monto pendiente · $simbolo %.2f".format(montoPendiente))
                }

                Spacer(Modifier.height(20.dp))
                Text("Descripción (opcional)", style = FinovaLabelLg, color = FinovaOnSurface)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej. Ahorro de la quincena...") },
                    leadingIcon = { Icon(Icons.Outlined.EditNote, null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                if (error != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(error!!, color = MaterialTheme.colorScheme.error, style = FinovaBodySm)
                }
                if (montoNumerico > saldoDisponible && cuentaSeleccionada != null) {
                    Spacer(Modifier.height(8.dp))
                    Text("El monto supera el saldo disponible de la cuenta.", color = MaterialTheme.colorScheme.error, style = FinovaBodySm)
                }
                if (montoNumerico > montoPendiente && !metaCompletada) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "El monto excede la meta. Solo puedes aportar hasta $simbolo %.2f para completarla.".format(montoPendiente),
                        color = MaterialTheme.colorScheme.error,
                        style = FinovaBodySm
                    )
                }
                if (metaCompletada) {
                    Spacer(Modifier.height(8.dp))
                    Text("Esta meta ya está completada. No admite más aportes.", color = FinovaPrimary, style = FinovaBodySm)
                }

                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = {
                        val cuenta = cuentaSeleccionada ?: return@Button
                        scope.launch {
                            enviando = true
                            error = null
                            try {
                                ApiClient.apiService.aportarAMeta(
                                    metaId,
                                    mapOf("cuentaId" to cuenta.id.toDouble(), "monto" to montoNumerico)
                                )
                                aporteCompletoMeta = montoNumerico >= montoPendiente
                                mostrarExito = true
                            } catch (e: Exception) {
                                error = e.message ?: "No se pudo registrar el aporte."
                            } finally { enviando = false }
                        }
                    },
                    enabled = puedeConfirmar,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FinovaPrimaryContainer, contentColor = FinovaOnPrimary)
                ) {
                    if (enviando) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = FinovaOnPrimary, strokeWidth = 2.dp)
                    else Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(if (enviando) "Procesando..." else "Confirmar depósito", style = FinovaLabelLg, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(12.dp)) {
                    Text("Cancelar", style = FinovaLabelLg, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (mostrarExito) {
        AlertDialog(
            onDismissRequest = { mostrarExito = false },
            confirmButton = {
                Button(onClick = { mostrarExito = false; onConfirmar() }) { Text("Entendido") }
            },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = FinovaPrimary,
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    if (aporteCompletoMeta) "¡Meta completada!" else "¡Depósito exitoso!",
                    style = FinovaHeadlineMd,
                    color = FinovaOnSurface
                )
            },
            text = {
                Text(
                    if (aporteCompletoMeta) {
                        "¡Felicitaciones! Has alcanzado tu objetivo de ahorro para la meta ${meta?.nombre ?: "seleccionada"}."
                    } else {
                        "Se han añadido $simbolo %.2f a la meta %s.".format(montoNumerico, meta?.nombre ?: "seleccionada")
                    },
                    style = FinovaBodyMd,
                    color = FinovaOnSurfaceVariant
                )
            }
        )
    }

    if (mostrarAvisoMetaCompletada) {
        AlertDialog(
            onDismissRequest = { mostrarAvisoMetaCompletada = false },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FinovaPrimary, modifier = Modifier.size(40.dp)) },
            title = { Text("Meta ya completada", style = FinovaHeadlineMd, color = FinovaOnSurface) },
            text = { Text("Esta meta ya alcanzó su monto objetivo. No necesitas realizar más aportes.", style = FinovaBodyMd, color = FinovaOnSurfaceVariant) },
            confirmButton = { Button(onClick = { mostrarAvisoMetaCompletada = false; onBack() }) { Text("Entendido") } }
        )
    }

}

@Composable
private fun AnadirFondosTopBar(onBack: () -> Unit) {
    Surface(color = FinovaSurface, shadowElevation = 1.dp) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver", tint = FinovaOnSurface) }
            Box(Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(FinovaPrimaryContainer), contentAlignment = Alignment.Center) {
                Text("F", style = FinovaLabelLg, color = FinovaOnPrimary)
            }
            Spacer(Modifier.width(10.dp))
            Text("Añadir Fondos", style = FinovaHeadlineSm, color = FinovaOnSurface, modifier = Modifier.weight(1f))
            IconButton(onClick = { }) { Icon(Icons.Default.MoreVert, "Más", tint = FinovaOnSurfaceVariant, modifier = Modifier.size(22.dp)) }
        }
    }
}

@Composable
private fun CuentaSelectorCard(titulo: String, numero: String, saldo: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(FinovaSurfaceContainerLowest)
            .clickable(onClick = onClick).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(FinovaSurfaceContainer), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.AccountBalance, null, tint = FinovaPrimaryContainer, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(titulo, style = FinovaHeadlineSm, color = FinovaOnSurface, maxLines = 1)
            Text("$numero · $saldo", style = FinovaBodySm, color = FinovaOnSurfaceVariant)
        }
        Icon(Icons.Default.ExpandMore, null, tint = FinovaOnSurfaceVariant)
    }
}

@Composable
private fun MetaSelectorCard(nombre: String, monto: String, porcentaje: Int) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(FinovaSurfaceContainerLowest).padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(FinovaSecondaryContainer), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Shield, null, tint = FinovaSecondary, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(nombre, style = FinovaHeadlineSm, color = FinovaOnSurface, maxLines = 1)
                Spacer(Modifier.height(2.dp))
                Text(monto, style = FinovaBodySm, color = FinovaOnSurfaceVariant)
            }
            Text("$porcentaje%", style = FinovaLabelSm, color = FinovaSecondary, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(progress = { porcentaje / 100f }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape), color = FinovaPrimary, trackColor = FinovaSurfaceContainer)
    }
}
