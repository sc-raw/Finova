package com.example.Finova.feature.cuentas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Finova.core.network.ApiClient
import com.example.Finova.core.network.dto.CuentaDto
import com.example.Finova.core.network.dto.TransferirRequestDto
import com.example.Finova.core.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CuentasViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)
    private val usuarioId: Long = sessionManager.obtenerUsuarioId()

    // ============ CUENTAS ============
    private val _cuentas = MutableStateFlow<List<CuentaDto>>(emptyList())
    val cuentas: StateFlow<List<CuentaDto>> = _cuentas

    // ============ ESTADOS ============
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // ============ TRANSFERENCIA ============
    private val _transferenciaCargando = MutableStateFlow(false)
    val transferenciaCargando: StateFlow<Boolean> = _transferenciaCargando

    private val _transferenciaError = MutableStateFlow<String?>(null)
    val transferenciaError: StateFlow<String?> = _transferenciaError

    private val _transferenciaExitosa = MutableStateFlow(false)
    val transferenciaExitosa: StateFlow<Boolean> = _transferenciaExitosa

    init {
        if (usuarioId != -1L) {
            cargarCuentas()
        } else {
            _error.value = "No hay sesión activa"
        }
    }

    fun cargarCuentas() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                _cuentas.value = ApiClient.apiService.listarCuentasPorUsuario(usuarioId)
            } catch (e: Exception) {
                _error.value = traducirError(e.message)
            } finally {
                _cargando.value = false
            }
        }
    }

    fun transferir(
        cuentaOrigenId: Long,
        cuentaDestinoId: Long,
        monto: Double,
        descripcion: String
    ) {
        if (cuentaOrigenId == cuentaDestinoId) {
            _transferenciaError.value = "Las cuentas deben ser diferentes"
            return
        }

        if (monto <= 0) {
            _transferenciaError.value = "El monto debe ser mayor a cero"
            return
        }

        val cuentaOrigen = _cuentas.value.find { it.id == cuentaOrigenId }
        if (cuentaOrigen != null && (cuentaOrigen.saldoActual ?: 0.0) < monto) {
            _transferenciaError.value = "Saldo insuficiente"
            return
        }

        viewModelScope.launch {
            _transferenciaCargando.value = true
            _transferenciaError.value = null

            try {
                ApiClient.apiService.transferir(
                    TransferirRequestDto(
                        cuentaOrigenId = cuentaOrigenId,
                        cuentaDestinoId = cuentaDestinoId,
                        monto = monto,
                        descripcion = descripcion.ifBlank { "Transferencia" }
                    )
                )
                _transferenciaExitosa.value = true
                cargarCuentas()
            } catch (e: Exception) {
                _transferenciaError.value = traducirError(e.message)
            } finally {
                _transferenciaCargando.value = false
            }
        }
    }

    fun resetearTransferencia() {
        _transferenciaError.value = null
        _transferenciaExitosa.value = false
    }

    private fun traducirError(mensaje: String?): String {
        return when {
            mensaje == null -> "Error desconocido"
            mensaje.contains("Connection refused") -> "No se pudo conectar al servidor"
            mensaje.contains("Unable to resolve host") -> "Sin conexión a Internet"
            mensaje.contains("Saldo insuficiente") -> "Saldo insuficiente"
            mensaje.contains("401") -> "Sesión expirada"
            else -> mensaje
        }
    }
}