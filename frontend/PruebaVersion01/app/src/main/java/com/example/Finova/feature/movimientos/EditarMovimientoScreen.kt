package com.example.Finova.feature.movimientos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.example.Finova.core.design.theme.FinovaPrimary
import com.example.Finova.core.network.ApiClient
import com.example.Finova.core.network.dto.CategoriaDto
import com.example.Finova.core.network.dto.MovimientoDto
import kotlinx.coroutines.launch
import com.example.Finova.core.network.dto.EditarMovimientoRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarMovimientoScreen(
    movimientoId: Long,
    onBack: () -> Unit,
    onGuardado: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var movimientoOriginal by remember { mutableStateOf<MovimientoDto?>(null) }
    var categorias by remember { mutableStateOf<List<CategoriaDto>>(emptyList()) }
    var categoriaSeleccionada by remember { mutableStateOf<CategoriaDto?>(null) }
    var titulo by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var menuCategoria by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(true) }
    var guardando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(movimientoId) {
        cargando = true
        error = null
        try {
            val movimiento = ApiClient.apiService.obtenerMovimiento(movimientoId)
            val listaCategorias = ApiClient.apiService.listarCategorias()
            movimientoOriginal = movimiento
            categorias = listaCategorias
            titulo = movimiento.titulo ?: movimiento.descripcion.orEmpty()
            monto = movimiento.monto?.toString().orEmpty()

            val tipoCategoria = movimiento.categoriaTipo
                ?: movimiento.tipoMovimiento
            val categoriasCompatibles = listaCategorias.filter {
                it.tipo.equals(tipoCategoria, ignoreCase = true)
            }
            categoriaSeleccionada = listaCategorias.firstOrNull {
                it.id == movimiento.categoriaId
            } ?: categoriasCompatibles.firstOrNull()
        } catch (e: Exception) {
            error = "No se pudieron cargar los datos del movimiento: ${e.message ?: ""}"
        } finally {
            cargando = false
        }
    }

    val movimiento = movimientoOriginal
    val tipoCategoria = movimiento?.categoriaTipo ?: movimiento?.tipoMovimiento
    val categoriasDisponibles = categorias.filter {
        it.tipo.equals(tipoCategoria, ignoreCase = true)
    }.ifEmpty { categorias }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar movimiento") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
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
                CircularProgressIndicator(color = FinovaPrimary)
                return@Column
            }

            if (movimiento == null) {
                Text(
                    text = error ?: "No se encontró el movimiento.",
                    color = MaterialTheme.colorScheme.error
                )
                return@Column
            }

            Text(
                text = "Solo puedes modificar el título, el monto y la categoría. Los demás datos se conservarán.",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

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

            Box(modifier = Modifier.fillMaxWidth()) {
                androidx.compose.material3.Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { menuCategoria = true },
                    shape = OutlinedTextFieldDefaults.shape,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline
                    )
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Text(
                            text = "Categoría",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = categoriaSeleccionada?.nombre ?: "Seleccionar categoría",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                DropdownMenu(
                    expanded = menuCategoria,
                    onDismissRequest = { menuCategoria = false }
                ) {
                    if (categoriasDisponibles.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No hay categorías disponibles") },
                            onClick = { menuCategoria = false }
                        )
                    } else {
                        categoriasDisponibles.forEach { categoria ->
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
            }

            error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    val valor = monto.toDoubleOrNull()
                    val categoria = categoriaSeleccionada
                    if (titulo.isBlank()) {
                        error = "Ingresa un título."
                        return@Button
                    }
                    if (valor == null || valor <= 0.0) {
                        error = "Ingresa un monto válido."
                        return@Button
                    }
                    if (categoria == null) {
                        error = "Selecciona una categoría."
                        return@Button
                    }

                    scope.launch {
                        guardando = true
                        error = null
                        try {
                            ApiClient.apiService.editarMovimiento(
                                id = movimiento.id,
                                request = EditarMovimientoRequest(
                                    titulo = titulo.trim(),
                                    monto = valor,
                                    categoriaId = categoria.id
                                )
                            )
                            onGuardado()
                        } catch (e: Exception) {
                            error = "No se pudieron guardar los cambios: ${e.message ?: "Error de conexión"}"
                        } finally {
                            guardando = false
                        }
                    }
                },
                enabled = !guardando && categoriaSeleccionada != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (guardando) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(8.dp))
                    Text("Guardando...")
                } else {
                    Text("Guardar cambios")
                }
            }
        }
    }
}
