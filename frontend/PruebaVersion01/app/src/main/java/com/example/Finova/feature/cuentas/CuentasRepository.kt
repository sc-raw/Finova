package com.example.Finova.feature.cuentas

import com.example.Finova.core.network.ApiClient
import com.example.Finova.core.network.dto.CuentaDto
import com.example.Finova.core.network.dto.TransferirRequestDto

class CuentasRepository {

    suspend fun obtenerCuentasPorUsuario(usuarioId: Long): List<CuentaDto> {
        return ApiClient.apiService.listarCuentasPorUsuario(usuarioId)
    }

    suspend fun obtenerCuenta(id: Long): CuentaDto {
        return ApiClient.apiService.obtenerCuenta(id)
    }

    suspend fun transferir(
        cuentaOrigenId: Long,
        cuentaDestinoId: Long,
        monto: Double,
        descripcion: String
    ) {
        ApiClient.apiService.transferir(
            TransferirRequestDto(
                cuentaOrigenId = cuentaOrigenId,
                cuentaDestinoId = cuentaDestinoId,
                monto = monto,
                descripcion = descripcion
            )
        )
    }
}