
package com.example.Finova.feature.movimientos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.Finova.core.design.theme.FinovaPrimary
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
import java.util.Locale

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
    var moneda by remember { mutableStateOf("PEN") }
    var monto by remember { mutableStateOf("") }
    var titulo by remember { mutableStateOf("") }
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



    val cuentasMoneda = cuentas.filter { it.moneda.equals(moneda, ignoreCase = true) }

    LaunchedEffect(moneda, cuentas) {
        if (cuentaSeleccionada?.moneda?.equals(moneda, ignoreCase = true) != true) {
            cuentaSeleccionada = cuentasMoneda.firstOrNull()
        }
    }

    LaunchedEffect(usuarioId) {
        try {
            cuentas = ApiClient.apiService.listarCuentasPorUsuario(usuarioId)
            categorias = ApiClient.apiService.listarCategorias()
            metodos = ApiClient.apiService.listarMetodosPagoActivos()
            cuentaSeleccionada = cuentas.firstOrNull { it.moneda.equals(moneda, ignoreCase = true) }
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
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(44.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = FinovaPrimary)
                }
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = FinovaPrimary
                ) {
                    Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Text("F", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    Text("FINOVA", color = FinovaPrimary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Text("Movimientos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }
            Text(
                text = "Selecciona el tipo de movimiento",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            if (cargando) {
                CircularProgressIndicator()
                return@Column
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                FilterChip(
                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp).heightIn(min = 64.dp),
                    selected = tipo == "GASTO",
                    onClick = { tipo = "GASTO" },
                    label = { Text("Gasto", fontWeight = FontWeight.Medium) },
                    leadingIcon = {
                        Icon(Icons.Filled.TrendingDown, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface,
                        iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedContainerColor = Color(0xFFDDF5EF),
                        selectedLabelColor = FinovaPrimary,
                        selectedLeadingIconColor = FinovaPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = tipo == "GASTO",
                        borderColor = Color(0xFFE0E0E0),
                        selectedBorderColor = Color(0xFF9DE4D2)
                    )
                )
                Spacer(Modifier.width(10.dp))
                FilterChip(
                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp).heightIn(min = 64.dp),
                    selected = tipo == "INGRESO",
                    onClick = { tipo = "INGRESO" },
                    label = { Text("Ingreso", fontWeight = FontWeight.Medium) },
                    leadingIcon = {
                        Icon(Icons.Filled.TrendingUp, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface,
                        iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedContainerColor = Color(0xFFDDF5EF),
                        selectedLabelColor = FinovaPrimary,
                        selectedLeadingIconColor = FinovaPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = tipo == "INGRESO",
                        borderColor = Color(0xFFE0E0E0),
                        selectedBorderColor = Color(0xFF9DE4D2)
                    )
                )
            }

            OutlinedTextField(
                value = monto,
                onValueChange = { valor ->
                    if (valor.matches(Regex("^\\d{0,10}(\\.\\d{0,2})?$"))) {
                        monto = valor
                    }
                },
                label = { Text(if (moneda == "PEN") "Monto (S/)" else "Monto ($)") },
                leadingIcon = { Text(if (moneda == "PEN") "S/" else "$", color = FinovaPrimary, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                FilterChip(
                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp).heightIn(min = 60.dp),
                    selected = moneda == "PEN",
                    onClick = { moneda = "PEN"; monto = ""; error = null },
                    label = { Text("Soles (S/)", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelLarge) },
                    leadingIcon = { Icon(Icons.Filled.AttachMoney, contentDescription = null, modifier = Modifier.size(20.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFDDF5EF),
                        selectedLabelColor = FinovaPrimary,
                        selectedLeadingIconColor = FinovaPrimary
                    )
                )
                Spacer(Modifier.width(10.dp))
                FilterChip(
                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp).heightIn(min = 60.dp),
                    selected = moneda == "USD",
                    onClick = { moneda = "USD"; monto = ""; error = null },
                    label = { Text("Dólares ($)", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelLarge) },
                    leadingIcon = { Icon(Icons.Filled.AttachMoney, contentDescription = null, modifier = Modifier.size(20.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFDDF5EF),
                        selectedLabelColor = FinovaPrimary,
                        selectedLeadingIconColor = FinovaPrimary
                    )
                )
            }

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                leadingIcon = { Icon(Icons.Filled.Description, contentDescription = null, tint = FinovaPrimary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = menuCuenta,
                onExpandedChange = { menuCuenta = !menuCuenta }
            ) {
                OutlinedTextField(
                    value = cuentaSeleccionada?.let { cuenta ->
                        "${cuenta.nombre} — ${if (moneda == "PEN") "S/" else "$"} ${String.format(Locale.US, "%.2f", cuenta.saldoActual ?: 0.0)}"
                    } ?: "Seleccionar cuenta",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Cuenta") },
                    leadingIcon = { Icon(Icons.Filled.AccountBalanceWallet, contentDescription = null, tint = FinovaPrimary) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(menuCuenta) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = menuCuenta,
                    onDismissRequest = { menuCuenta = false }
                ) {
                    cuentasMoneda.forEach { cuenta ->
                        DropdownMenuItem(
                            text = {
                                Text("${cuenta.nombre} — ${if (moneda == "PEN") "S/" else "$"} ${String.format(Locale.US, "%.2f", cuenta.saldoActual ?: 0.0)}")
                            },
                            onClick = {
                                cuentaSeleccionada = cuenta
                                menuCuenta = false
                            }
                        )
                    }
                }
            }

            if (cuentasMoneda.isEmpty()) {
                Text(
                    text = "No dispones de una cuenta en ${if (moneda == "PEN") "soles" else "dólares"}.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
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
                    leadingIcon = { Icon(Icons.Filled.Category, contentDescription = null, tint = FinovaPrimary) },
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
                    leadingIcon = { Icon(Icons.Filled.CreditCard, contentDescription = null, tint = FinovaPrimary) },
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
                        error = if (cuenta == null) "Selecciona una cuenta en ${if (moneda == "PEN") "soles" else "dólares"}." else "Selecciona categoría y método de pago."
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
                                        moneda = moneda,
                                        titulo = titulo.trim().ifBlank { null },
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
                                        moneda = moneda,
                                        titulo = titulo.trim().ifBlank { null },
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
                        cuentasMoneda.isNotEmpty() &&
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
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar movimiento", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
