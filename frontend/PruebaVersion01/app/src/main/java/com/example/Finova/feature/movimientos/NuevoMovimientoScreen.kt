
package com.example.Finova.feature.movimientos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Finova.core.network.ApiClient
import com.example.Finova.core.network.dto.*
import com.example.Finova.core.util.SessionManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoMovimientoScreen(
    onBack: () -> Unit,
    onGuardado: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val session = remember { SessionManager(context) }
    val usuarioId = remember { session.obtenerUsuarioId() }

    var tipo by remember { mutableStateOf("GASTO") }
    var monto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    var cuentas by remember { mutableStateOf<List<CuentaDto>>(emptyList()) }
    var categorias by remember { mutableStateOf<List<CategoriaDto>>(emptyList()) }
    var metodos by remember { mutableStateOf<List<MetodoPagoDto>>(emptyList()) }

    var cuentaSeleccionada by remember { mutableStateOf<CuentaDto?>(null) }
    var categoriaSeleccionada by remember { mutableStateOf<CategoriaDto?>(null) }
    var metodoSeleccionado by remember { mutableStateOf<MetodoPagoDto?>(null) }

    var menuCuenta by remember { mutableStateOf(false) }
    var menuCategoria by remember { mutableStateOf(false) }
    var menuMetodo by remember { mutableStateOf(false) }

    var cargando by remember { mutableStateOf(true) }
    var guardando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(usuarioId) {
        try {
            cuentas = ApiClient.apiService.listarCuentasPorUsuario(usuarioId)
            categorias = ApiClient.apiService.listarCategorias()
            metodos = ApiClient.apiService.listarMetodosPagoActivos()
            cuentaSeleccionada = cuentas.firstOrNull()
            categoriaSeleccionada = categorias.firstOrNull {
                it.tipo.equals(tipo, ignoreCase = true)
            }
            metodoSeleccionado = metodos.firstOrNull()
        } catch (e: Exception) {
            error = "No se pudieron cargar los datos: ${e.message ?: "Error de conexión"}"
        } finally {
            cargando = false
        }
    }

    LaunchedEffect(tipo, categorias) {
        categoriaSeleccionada = categorias.firstOrNull {
            it.tipo.equals(tipo, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar movimiento") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Volver") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (cargando) {
                CircularProgressIndicator()
                return@Column
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = tipo == "GASTO",
                    onClick = { tipo = "GASTO" },
                    label = { Text("Gasto") }
                )
                FilterChip(
                    selected = tipo == "INGRESO",
                    onClick = { tipo = "INGRESO" },
                    label = { Text("Ingreso") }
                )
            }

            OutlinedTextField(
                value = monto,
                onValueChange = { valor ->
                    if (valor.matches(Regex("^\\d{0,10}(\\.\\d{0,2})?$"))) {
                        monto = valor
                    }
                },
                label = { Text("Monto (S/)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = menuCuenta,
                onExpandedChange = { menuCuenta = !menuCuenta }
            ) {
                OutlinedTextField(
                    value = cuentaSeleccionada?.nombre ?: "Seleccionar cuenta",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Cuenta") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(menuCuenta) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = menuCuenta,
                    onDismissRequest = { menuCuenta = false }
                ) {
                    cuentas.forEach { cuenta ->
                        DropdownMenuItem(
                            text = {
                                Text("${cuenta.nombre} — S/ ${cuenta.saldoActual ?: 0.0}")
                            },
                            onClick = {
                                cuentaSeleccionada = cuenta
                                menuCuenta = false
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = menuCategoria,
                onExpandedChange = { menuCategoria = !menuCategoria }
            ) {
                OutlinedTextField(
                    value = categoriaSeleccionada?.nombre ?: "Seleccionar categoría",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(menuCategoria)
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = menuCategoria,
                    onDismissRequest = { menuCategoria = false }
                ) {
                    categorias
                        .filter { it.tipo.equals(tipo, ignoreCase = true) }
                        .forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria.nombre ?: "Sin nombre") },
                                onClick = {
                                    categoriaSeleccionada = categoria
                                    menuCategoria = false
                                }
                            )
                        }
                }
            }

            ExposedDropdownMenuBox(
                expanded = menuMetodo,
                onExpandedChange = { menuMetodo = !menuMetodo }
            ) {
                OutlinedTextField(
                    value = metodoSeleccionado?.nombre ?: "Seleccionar método",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Método de pago") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(menuMetodo)
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = menuMetodo,
                    onDismissRequest = { menuMetodo = false }
                ) {
                    metodos.forEach { metodo ->
                        DropdownMenuItem(
                            text = { Text(metodo.nombre ?: "Sin nombre") },
                            onClick = {
                                metodoSeleccionado = metodo
                                menuMetodo = false
                            }
                        )
                    }
                }
            }

            error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    val valor = monto.toDoubleOrNull()
                    if (valor == null || valor <= 0.0) {
                        error = "Ingresa un monto válido."
                        return@Button
                    }
                    if (usuarioId <= 0L) {
                        error = "No hay una sesión válida. Vuelve a iniciar sesión."
                        return@Button
                    }
                    val cuenta = cuentaSeleccionada
                    val categoria = categoriaSeleccionada
                    val metodo = metodoSeleccionado
                    if (cuenta == null || categoria == null || metodo == null) {
                        error = "Selecciona cuenta, categoría y método de pago."
                        return@Button
                    }

                    scope.launch {
                        guardando = true
                        error = null
                        try {
                            if (tipo == "GASTO") {
                                ApiClient.apiService.registrarGasto(
                                    RegistrarGastoRequestDto(
                                        cuentaId = cuenta.id,
                                        categoriaId = categoria.id,
                                        metodoPagoId = metodo.id,
                                        monto = valor,
                                        descripcion = descripcion.trim().ifBlank { null }
                                    )
                                )
                            } else {
                                ApiClient.apiService.registrarIngreso(
                                    RegistrarIngresoRequestDto(
                                        cuentaId = cuenta.id,
                                        categoriaId = categoria.id,
                                        metodoPagoId = metodo.id,
                                        monto = valor,
                                        descripcion = descripcion.trim().ifBlank { null }
                                    )
                                )
                            }
                            onGuardado()
                        } catch (e: Exception) {
                            error = when {
                                e.message.orEmpty().contains("400") ->
                                    "Datos inválidos o saldo insuficiente."
                                e.message.orEmpty().contains("401") ->
                                    "La sesión expiró. Vuelve a iniciar sesión."
                                else ->
                                    "No se pudo guardar el movimiento. ${e.message ?: ""}"
                            }
                        } finally {
                            guardando = false
                        }
                    }
                },
                enabled = !guardando &&
                        cuentas.isNotEmpty() &&
                        categorias.any { it.tipo.equals(tipo, ignoreCase = true) } &&
                        metodos.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (guardando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Guardando...")
                } else {
                    Text("Guardar movimiento")
                }
            }
        }
    }
}
