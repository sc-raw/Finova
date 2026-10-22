package com.example.Finova.core.network.dto

data class CuentaReferenciaRequestDto(val id: Long)

data class CuentaRequestDto(
    val usuario: CuentaReferenciaRequestDto,
    val banco: CuentaReferenciaRequestDto,
    val numeroCuenta: String,
    val moneda: String,
    val nombre: String,
    val saldoInicial: Double,
    val saldoActual: Double
)
