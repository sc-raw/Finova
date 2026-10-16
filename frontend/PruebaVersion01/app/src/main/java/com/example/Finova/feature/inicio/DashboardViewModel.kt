package com.example.Finova.feature.inicio

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Finova.core.network.ApiClient
import com.example.Finova.core.network.dto.CuentaDto
import com.example.Finova.core.network.dto.MovimientoDto
import com.example.Finova.core.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)
    private val usuarioId: Long = sessionManager.obtenerUsuarioId()

    // ============ CUENTAS ============
    private val _cuentas = MutableStateFlow<List<CuentaDto>>(emptyList())
    val cuentas: StateFlow<List<CuentaDto>> = _cuentas

    // ============ MOVIMIENTOS ============
    private val _movimientos = MutableStateFlow<List<MovimientoDto>>(emptyList())
    val movimientos: StateFlow<List<MovimientoDto>> = _movimientos

    // ============ ESTADOS ============
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // ============ DATOS DEL USUARIO ============
    val nombresUsuario: String = sessionManager.obtenerNombres()

    // ============ DATOS CALCULADOS ============
    val saldoTotalSoles: Double
        get() = _cuentas.value
            .filter { it.moneda == "PEN" }
            .sumOf { it.saldoActual ?: 0.0 }

    val saldoTotalUsd: Double
        get() = _cuentas.value
            .filter { it.moneda == "USD" }
            .sumOf { it.saldoActual ?: 0.0 }

    val ingresosMes: Double
        get() = _movimientos.value
            .filter { it.tipoMovimiento == "INGRESO" }
            .sumOf { it.monto ?: 0.0 }

    val gastosMes: Double
        get() = _movimientos.value
            .filter { it.tipoMovimiento == "GASTO" }
            .sumOf { it.monto ?: 0.0 }

    val tipoCambio: Double = 3.75 // TODO: Puedes traerlo de una API después

    init {
        if (usuarioId != -1L) {
            cargarDashboard()
        } else {
            _error.value = "No hay sesión activa"
        }
    }

    fun cargarDashboard() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            try {
                _cuentas.value = ApiClient.apiService.listarCuentasPorUsuario(usuarioId)
                _movimientos.value = ApiClient.apiService.listarMovimientosPorUsuario(usuarioId)
            } catch (e: Exception) {
                _error.value = traducirError(e.message)
            } finally {
                _cargando.value = false
            }
        }
    }

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