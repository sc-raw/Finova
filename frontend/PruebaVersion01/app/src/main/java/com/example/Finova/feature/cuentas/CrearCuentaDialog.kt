package com.example.Finova.feature.cuentas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.Finova.core.network.ApiClient
import com.example.Finova.core.network.dto.BancoDto
import com.example.Finova.core.network.dto.CuentaDto
import com.example.Finova.core.network.dto.CuentaRequestDto
import com.example.Finova.core.network.dto.CuentaReferenciaRequestDto
import com.example.Finova.core.network.dto.TransferirRequestDto
import com.example.Finova.core.util.SessionManager
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CrearCuentaDialog(
    cuentasActuales: List<CuentaDto>,
    onDismiss: () -> Unit,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    val usuarioId = remember { SessionManager(context).obtenerUsuarioId() }
    val scope = rememberCoroutineScope()
    var bancos by remember { mutableStateOf<List<BancoDto>>(emptyList()) }
    var bancoId by remember { mutableStateOf<Long?>(null) }
    var nombre by remember { mutableStateOf("") }
    var numeroCuenta by remember { mutableStateOf("") }
    var moneda by remember { mutableStateOf("PEN") }
    var saldoInicial by remember { mutableStateOf("0.00") }
    var bancoExpanded by remember { mutableStateOf(false) }
    var monedaExpanded by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            bancos = ApiClient.apiService.listarBancosActivos()
            bancoId = bancos.firstOrNull()?.id
        } catch (e: Exception) {
            error = "No se pudieron cargar los bancos: ${e.message ?: "error de conexión"}"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva cuenta") },
        text = {
            Column(Modifier.fillMaxWidth().heightIn(max = 520.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre de la cuenta") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = numeroCuenta, onValueChange = { numeroCuenta = it }, label = { Text("Número de cuenta") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                ExposedDropdownMenuBox(expanded = bancoExpanded, onExpandedChange = { bancoExpanded = !bancoExpanded }) {
                    OutlinedTextField(
                        value = bancos.firstOrNull { it.id == bancoId }?.nombre.orEmpty(),
                        onValueChange = {}, readOnly = true, label = { Text("Banco") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bancoExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = bancoExpanded, onDismissRequest = { bancoExpanded = false }) {
                        bancos.forEach { banco ->
                            DropdownMenuItem(text = { Text(banco.nombre ?: "Banco") }, onClick = { bancoId = banco.id; bancoExpanded = false })
                        }
                    }
                }
                ExposedDropdownMenuBox(expanded = monedaExpanded, onExpandedChange = { monedaExpanded = !monedaExpanded }) {
                    OutlinedTextField(value = moneda, onValueChange = {}, readOnly = true, label = { Text("Moneda") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = monedaExpanded) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                    ExposedDropdownMenu(expanded = monedaExpanded, onDismissRequest = { monedaExpanded = false }) {
                        listOf("PEN", "USD").forEach { item -> DropdownMenuItem(text = { Text(if (item == "PEN") "Soles (PEN)" else "Dólares (USD)") }, onClick = { moneda = item; monedaExpanded = false }) }
                    }
                }
                OutlinedTextField(value = saldoInicial, onValueChange = { saldoInicial = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Saldo inicial") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            TextButton(enabled = !cargando, onClick = {
                val saldo = saldoInicial.toDoubleOrNull()
                when {
                    usuarioId <= 0 -> error = "No hay una sesión válida."
                    nombre.isBlank() -> error = "Ingresa el nombre de la cuenta."
                    numeroCuenta.isBlank() -> error = "Ingresa el número de cuenta."
                    bancoId == null -> error = "Selecciona un banco."
                    saldo == null || saldo < 0 -> error = "Ingresa un saldo inicial válido."
                    else -> scope.launch {
                        cargando = true; error = null
                        try {
                            ApiClient.apiService.guardarCuenta(
                                CuentaRequestDto(
                                    usuario = CuentaReferenciaRequestDto(usuarioId),
                                    banco = CuentaReferenciaRequestDto(bancoId!!),
                                    numeroCuenta = numeroCuenta.trim(),
                                    moneda = moneda,
                                    nombre = nombre.trim(),
                                    saldoInicial = saldo,
                                    saldoActual = saldo
                                )
                            )
                            onComplete()
                        } catch (e: Exception) {
                            error = "No se pudo crear la cuenta: ${e.message ?: "error de conexión"}"
                        } finally { cargando = false }
                    }
                }
            }) { Text(if (cargando) "Guardando…" else "Crear cuenta") }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !cargando) { Text("Cancelar") } }
    )
}

@Composable
internal fun EliminarCuentaDialog(
    cuentas: List<CuentaDto>,
    onDismiss: () -> Unit,
    onComplete: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var cuentaEliminarId by remember { mutableStateOf<Long?>(null) }
    var cuentaDestinoId by remember { mutableStateOf<Long?>(null) }
    var cargando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val cuentasOrdenadas = cuentas.sortedBy { it.id }
    val principalId = cuentasOrdenadas.firstOrNull()?.id
    val eliminable = cuentasOrdenadas.filter { it.id != principalId }
    val cuentaEliminar = cuentas.firstOrNull { it.id == cuentaEliminarId }
    val esUsd = cuentaEliminar?.moneda == "USD"
    val saldoEliminar = cuentaEliminar?.saldoActual ?: 0.0
    val usdDebeQuedarEnCero = esUsd && saldoEliminar != 0.0
    val destinos = cuentas.filter { it.id != cuentaEliminarId && (cuentaEliminar == null || it.moneda == cuentaEliminar.moneda) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar cuenta") },
        text = {
            Column(Modifier.fillMaxWidth().heightIn(max = 520.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Selecciona la cuenta que deseas eliminar.", style = MaterialTheme.typography.bodyMedium)
                if (cuentasOrdenadas.size <= 1) Text("No puedes eliminar la única cuenta registrada.", color = MaterialTheme.colorScheme.error)
                eliminable.forEach { cuenta ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                        RadioButton(selected = cuentaEliminarId == cuenta.id, onClick = { cuentaEliminarId = cuenta.id; cuentaDestinoId = null })
                        Column(Modifier.padding(top = 10.dp)) {
                            Text(cuenta.nombre ?: "Cuenta")
                            Text("Saldo: ${String.format(Locale.US, "%.2f", cuenta.saldoActual ?: 0.0)} ${cuenta.moneda ?: "PEN"}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                if (cuentaEliminarId != null) {
                    if (usdDebeQuedarEnCero) {
                        Text(
                            "Para eliminar una cuenta en dólares, primero debes dejar su saldo en US$ 0.00. Usa Transferir y vuelve aquí cuando el saldo esté en cero.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (esUsd) {
                        Text(
                            "El saldo está en cero. Puedes eliminar esta cuenta sin realizar una transferencia.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        Text("¿A qué cuenta transferimos el saldo?", style = MaterialTheme.typography.titleSmall)
                        destinos.forEach { cuenta ->
                            Row(Modifier.fillMaxWidth()) {
                                RadioButton(selected = cuentaDestinoId == cuenta.id, onClick = { cuentaDestinoId = cuenta.id })
                                Column(Modifier.padding(top = 10.dp)) {
                                    Text(cuenta.nombre ?: "Cuenta")
                                    Text("${cuenta.moneda ?: "PEN"} · ${String.format(Locale.US, "%.2f", cuenta.saldoActual ?: 0.0)}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                    Text("La cuenta principal no se puede eliminar.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                }
                if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            TextButton(enabled = !cargando && cuentaEliminarId != null && !usdDebeQuedarEnCero && (esUsd || cuentaDestinoId != null), onClick = {
                val origen = cuentaEliminar
                val destinoId = cuentaDestinoId
                if (origen == null || destinoId == null) return@TextButton
                scope.launch {
                    cargando = true; error = null
                    try {
                        if (origen.moneda == "USD") {
                            ApiClient.apiService.eliminarCuenta(origen.id)
                        } else {
                            if (destinoId == null) return@launch
                            val saldo = origen.saldoActual ?: 0.0
                            if (saldo > 0.0) {
                                ApiClient.apiService.transferir(
                                    TransferirRequestDto(
                                        cuentaOrigenId = origen.id,
                                        cuentaDestinoId = destinoId,
                                        monto = saldo,
                                        descripcion = "Transferencia por eliminación de cuenta"
                                    )
                                )
                            }
                            ApiClient.apiService.eliminarCuenta(origen.id)
                        }
                        onComplete()
                    } catch (e: Exception) {
                        error = "No se pudo eliminar la cuenta: ${e.message ?: "error de conexión"}"
                    } finally { cargando = false }
                }
            }) { Text(if (cargando) "Procesando…" else if (esUsd) "Eliminar cuenta" else "Transferir y eliminar") }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !cargando) { Text("Cancelar") } }
    )
}
