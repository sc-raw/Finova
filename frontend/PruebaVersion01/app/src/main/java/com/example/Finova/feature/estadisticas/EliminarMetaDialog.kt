package com.example.Finova.feature.estadisticas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Finova.core.network.ApiClient
import com.example.Finova.core.network.dto.CuentaDto
import com.example.Finova.core.network.dto.MetaDto
import com.example.Finova.core.util.SessionManager
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
internal fun EliminarMetaDialog(
    metas: List<MetaDto>,
    usuarioId: Long,
    onDismiss: () -> Unit,
    onEliminada: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var cuentas by remember { mutableStateOf<List<CuentaDto>>(emptyList()) }
    var metaSeleccionada by remember { mutableStateOf<MetaDto?>(null) }
    var cuentaSeleccionadaId by remember { mutableStateOf<Long?>(null) }
    var cargandoCuentas by remember { mutableStateOf(true) }
    var procesando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var confirmar by remember { mutableStateOf(false) }

    LaunchedEffect(usuarioId) {
        try {
            if (usuarioId <= 0L) error = "No hay una sesión activa."
            else cuentas = ApiClient.apiService.listarCuentasPorUsuario(usuarioId)
        } catch (e: Exception) {
            error = e.message ?: "No se pudieron cargar las cuentas."
        } finally {
            cargandoCuentas = false
        }
    }

    AlertDialog(
        onDismissRequest = { if (!procesando) onDismiss() },
        title = { Text(if (confirmar) "Confirmar eliminación" else "Eliminar meta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (!confirmar) {
                    Text("Selecciona la meta que deseas eliminar.")
                    if (metas.isEmpty()) Text("No tienes metas para eliminar.")
                    else LazyColumn(modifier = Modifier.heightIn(max = 180.dp)) {
                        items(metas) { meta ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    metaSeleccionada = meta
                                    cuentaSeleccionadaId = null
                                    error = null
                                }.padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = metaSeleccionada?.id == meta.id, onClick = {
                                    metaSeleccionada = meta
                                    cuentaSeleccionadaId = null
                                    error = null
                                })
                                Column(Modifier.weight(1f)) {
                                    Text(meta.nombre.orEmpty(), fontWeight = FontWeight.SemiBold)
                                    Text("Ahorrado: ${if ((meta.moneda ?: "PEN").uppercase() == "USD") "US$" else "S/"} ${String.format(Locale.US, "%.2f", meta.montoActual ?: 0.0)}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                    if (metaSeleccionada != null) {
                        Divider()
                        Text("Selecciona la cuenta donde devolver el dinero:", fontWeight = FontWeight.SemiBold)
                        when {
                            cargandoCuentas -> CircularProgressIndicator()
                            cuentas.filter { (it.moneda ?: "PEN").uppercase() == (metaSeleccionada?.moneda ?: "PEN").uppercase() }.isEmpty() -> Text("No tienes cuentas en la moneda de esta meta.")
                            else -> LazyColumn(modifier = Modifier.heightIn(max = 160.dp)) {
                                items(cuentas.filter { (it.moneda ?: "PEN").uppercase() == (metaSeleccionada?.moneda ?: "PEN").uppercase() }) { cuenta ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().clickable {
                                            cuentaSeleccionadaId = cuenta.id
                                            error = null
                                        }.padding(vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(selected = cuentaSeleccionadaId == cuenta.id, onClick = {
                                            cuentaSeleccionadaId = cuenta.id
                                            error = null
                                        })
                                        Column(Modifier.weight(1f)) {
                                            Text(cuenta.nombre ?: cuenta.bancoNombre ?: "Cuenta ${cuenta.id}")
                                            Text("Saldo: ${if ((cuenta.moneda ?: "PEN").uppercase() == "USD") "US$" else "S/"} ${String.format(Locale.US, "%.2f", cuenta.saldoActual ?: 0.0)}", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    val meta = metaSeleccionada
                    Text("Se devolverá el dinero ahorrado a la cuenta elegida y luego se eliminará esta meta.")
                    if (meta != null) {
                        Text(meta.nombre.orEmpty(), fontWeight = FontWeight.Bold)
                        Text("Devolución: ${if ((meta.moneda ?: "PEN").uppercase() == "USD") "US$" else "S/"} ${String.format(Locale.US, "%.2f", meta.montoActual ?: 0.0)}")
                    }
                }
                if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            TextButton(
                enabled = !procesando && !cargandoCuentas && (if (confirmar) metaSeleccionada != null else metaSeleccionada != null && cuentaSeleccionadaId != null),
                onClick = {
                    if (!confirmar) confirmar = true
                    else {
                        val meta = metaSeleccionada ?: return@TextButton
                        val cuentaId = cuentaSeleccionadaId ?: return@TextButton
                        procesando = true
                        error = null
                        scope.launch {
                            try {
                                ApiClient.apiService.eliminarMetaConDevolucion(
                                    meta.id,
                                    mapOf("cuentaDestinoId" to cuentaId)
                                )
                                onEliminada()
                            } catch (e: Exception) {
                                error = e.message ?: "No se pudo eliminar la meta."
                                confirmar = false
                            } finally {
                                procesando = false
                            }
                        }
                    }
                }
            ) {
                if (procesando) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                else Text(if (confirmar) "Confirmar" else "Continuar")
            }
        },
        dismissButton = {
            TextButton(onClick = { if (confirmar) confirmar = false else onDismiss() }, enabled = !procesando) {
                Text(if (confirmar) "Volver" else "Cancelar")
            }
        }
    )
}
