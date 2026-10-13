package com.example.Finova.feature.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ============================================================
// FINOVA — Dashboard ViewModel
// Datos mock por ahora. Cuando el backend esté listo, se
// reemplaza por llamadas a CuentaRepository / MovimientoRepository.
// ============================================================

data class MovimientoItem(
    val id: String,
    val titulo: String,
    val subtitulo: String,
    val monto: Double,
    val esIngreso: Boolean,
    val cuenta: String,
    val categoriaIcon: CategoriaIcon,
    val tieneIa: Boolean = false
)

enum class CategoriaIcon {
    SUPERMERCADO, TRANSFERENCIA, CAFE, SERVICIO
}

data class DashboardUiState(
    val nombreUsuario: String = "Diego",
    val mes: String = "Marzo 2025",
    val saldoTotalSoles: Double = 8450.20,
    val saldoTotalUsd: Double = 2253.38,
    val tipoCambio: Double = 3.75,
    val ingresosMes: Double = 4500.00,
    val gastosMes: Double = 1820.50,
    val presupuestoUsadoPct: Int = 74,
    val presupuestoRestante: Double = 1420.00,
    val presupuestoTotal: Double = 5500.00,
    val saldosOcultos: Boolean = false,
    val movimientos: List<MovimientoItem> = emptyList(),
    val cargando: Boolean = true
)

class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        cargarDatosMock()
    }

    private fun cargarDatosMock() {
        viewModelScope.launch {
            delay(400) // simula latencia de red
            _uiState.update {
                it.copy(
                    movimientos = listOf(
                        MovimientoItem(
                            id = "1",
                            titulo = "Supermercado Metro",
                            subtitulo = "Alimentación · Hoy, 13:45",
                            monto = 142.50,
                            esIngreso = false,
                            cuenta = "BCP ••4821",
                            categoriaIcon = CategoriaIcon.SUPERMERCADO,
                            tieneIa = true
                        ),
                        MovimientoItem(
                            id = "2",
                            titulo = "Transferencia Sueldo",
                            subtitulo = "Salario mensual · Ayer, 09:00",
                            monto = 3800.00,
                            esIngreso = true,
                            cuenta = "BCP Soles",
                            categoriaIcon = CategoriaIcon.TRANSFERENCIA
                        ),
                        MovimientoItem(
                            id = "3",
                            titulo = "Starbucks Coffee",
                            subtitulo = "Café & Salidas · Ayer, 18:20",
                            monto = 24.50,
                            esIngreso = false,
                            cuenta = "Efectivo",
                            categoriaIcon = CategoriaIcon.CAFE
                        ),
                        MovimientoItem(
                            id = "4",
                            titulo = "Recibo Luz Enel",
                            subtitulo = "Servicios del hogar · 18 Feb",
                            monto = 85.00,
                            esIngreso = false,
                            cuenta = "BCP ••4821",
                            categoriaIcon = CategoriaIcon.SERVICIO
                        )
                    ),
                    cargando = false
                )
            }
        }
    }

    /** Alterna visibilidad de saldos (botón del ojo) */
    fun toggleSaldos() {
        _uiState.update { it.copy(saldosOcultos = !it.saldosOcultos) }
    }
}