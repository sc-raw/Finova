package com.example.Finova.feature.movimientos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Finova.core.network.dto.MovimientoDto
import com.example.Finova.core.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class FiltroMovimiento {
    TODOS, INGRESOS, GASTOS
}

class MovimientosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MovimientosRepository()
    private val sessionManager = SessionManager(application)
    private val usuarioId: Long = sessionManager.obtenerUsuarioId()

    // ============ LISTA DE MOVIMIENTOS ============
    private val _movimientos = MutableStateFlow<List<MovimientoDto>>(emptyList())
    val movimientos: StateFlow<List<MovimientoDto>> = _movimientos

    private val _movimientosFiltrados = MutableStateFlow<List<MovimientoDto>>(emptyList())
    val movimientosFiltrados: StateFlow<List<MovimientoDto>> = _movimientosFiltrados

    // ============ DETALLE ============
    private val _movimientoDetalle = MutableStateFlow<MovimientoDto?>(null)
    val movimientoDetalle: StateFlow<MovimientoDto?> = _movimientoDetalle

    // ============ FILTRO ACTUAL ============
    private val _filtroActual = MutableStateFlow(FiltroMovimiento.TODOS)
    val filtroActual: StateFlow<FiltroMovimiento> = _filtroActual

    // ============ BÚSQUEDA ============
    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda

    // ============ ESTADOS ============
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // ============ OPERACIONES ============
    private val _eliminando = MutableStateFlow(false)
    val eliminando: StateFlow<Boolean> = _eliminando

    private val _eliminadoExitoso = MutableStateFlow(false)
    val eliminadoExitoso: StateFlow<Boolean> = _eliminadoExitoso

    init {
        if (usuarioId != -1L) {
            cargarMovimientos()
        } else {
            _error.value = "No hay sesión activa"
        }
    }

    // ============ CARGAR ============
    fun cargarMovimientos() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                val lista = repository.obtenerMovimientosPorUsuario(usuarioId)
                _movimientos.value = lista
                aplicarFiltro(_filtroActual.value, _textoBusqueda.value)
            } catch (e: Exception) {
                _error.value = traducirError(e.message)
            } finally {
                _cargando.value = false
            }
        }
    }

    fun cargarMovimientoDetalle(id: Long) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                _movimientoDetalle.value = repository.obtenerMovimiento(id)
            } catch (e: Exception) {
                _error.value = traducirError(e.message)
            } finally {
                _cargando.value = false
            }
        }
    }

    // ============ FILTROS ============
    fun cambiarFiltro(filtro: FiltroMovimiento) {
        _filtroActual.value = filtro
        aplicarFiltro(filtro, _textoBusqueda.value)
    }

    fun actualizarBusqueda(texto: String) {
        _textoBusqueda.value = texto
        aplicarFiltro(_filtroActual.value, texto)
    }

    private fun aplicarFiltro(filtro: FiltroMovimiento, busqueda: String) {
        var lista = _movimientos.value

        // Filtro por tipo
        lista = when (filtro) {
            FiltroMovimiento.TODOS -> lista
            FiltroMovimiento.INGRESOS -> lista.filter { it.tipoMovimiento == "INGRESO" }
            FiltroMovimiento.GASTOS -> lista.filter { it.tipoMovimiento == "GASTO" }
        }

        // Filtro por búsqueda
        if (busqueda.isNotBlank()) {
            lista = lista.filter {
                (it.descripcion ?: "").contains(busqueda, ignoreCase = true) ||
                        (it.categoriaNombre ?: "").contains(busqueda, ignoreCase = true)
            }
        }

        _movimientosFiltrados.value = lista
    }

    // ============ ELIMINAR ============
    fun eliminarMovimiento(id: Long) {
        viewModelScope.launch {
            _eliminando.value = true
            try {
                repository.eliminarMovimiento(id)
                _eliminadoExitoso.value = true
                cargarMovimientos()
            } catch (e: Exception) {
                _error.value = traducirError(e.message)
            } finally {
                _eliminando.value = false
            }
        }
    }

    fun resetearEliminado() {
        _eliminadoExitoso.value = false
    }

    // ============ CÁLCULOS ============
    fun totalIngresos(): Double =
        _movimientosFiltrados.value
            .filter { it.tipoMovimiento == "INGRESO" }
            .sumOf { it.monto ?: 0.0 }

    fun totalGastos(): Double =
        _movimientosFiltrados.value
            .filter { it.tipoMovimiento == "GASTO" }
            .sumOf { it.monto ?: 0.0 }

    // ============ UTIL ============
    private fun traducirError(mensaje: String?): String {
        return when {
            mensaje == null -> "Error desconocido"
            mensaje.contains("Connection refused") -> "No se pudo conectar al servidor"
            mensaje.contains("Unable to resolve host") -> "Sin conexión a Internet"
            mensaje.contains("401") -> "Sesión expirada"
            else -> mensaje
        }
    }
}