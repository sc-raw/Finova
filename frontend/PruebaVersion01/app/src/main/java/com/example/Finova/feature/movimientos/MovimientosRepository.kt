package com.example.Finova.feature.movimientos

import com.example.Finova.core.network.ApiClient
import com.example.Finova.core.network.dto.MovimientoDto
import com.example.Finova.core.network.dto.RegistrarGastoRequestDto
import com.example.Finova.core.network.dto.RegistrarIngresoRequestDto

class MovimientosRepository {

    suspend fun obtenerMovimientosPorUsuario(usuarioId: Long): List<MovimientoDto> {
        return ApiClient.apiService.listarMovimientosPorUsuario(usuarioId)
    }

    suspend fun obtenerMovimientosPorCuenta(cuentaId: Long): List<MovimientoDto> {
        return ApiClient.apiService.listarMovimientosPorCuenta(cuentaId)
    }

    suspend fun obtenerMovimiento(id: Long): MovimientoDto {
        return ApiClient.apiService.obtenerMovimiento(id)
    }

    suspend fun eliminarMovimiento(id: Long) {
        ApiClient.apiService.eliminarMovimiento(id)
    }

    suspend fun registrarGasto(request: RegistrarGastoRequestDto) {
        ApiClient.apiService.registrarGasto(request)
    }

    suspend fun registrarIngreso(request: RegistrarIngresoRequestDto) {
        ApiClient.apiService.registrarIngreso(request)
    }

    suspend fun listarPorTipoYUsuario(tipo: String, usuarioId: Long): List<MovimientoDto> {
        return ApiClient.apiService.listarMovimientosPorTipoYUsuario(tipo, usuarioId)
    }
}