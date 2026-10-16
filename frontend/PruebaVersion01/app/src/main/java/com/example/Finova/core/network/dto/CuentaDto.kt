package com.example.Finova.core.network.dto

import com.google.gson.annotations.SerializedName

data class CuentaDto(
    @SerializedName("id") val id: Long,
    @SerializedName("usuarioId") val usuarioId: Long?,
    @SerializedName("bancoId") val bancoId: Long?,
    @SerializedName("bancoNombre") val bancoNombre: String?,
    @SerializedName("numeroCuenta") val numeroCuenta: String?,
    @SerializedName("moneda") val moneda: String?,       // "PEN" o "USD"
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("saldoInicial") val saldoInicial: Double?,
    @SerializedName("saldoActual") val saldoActual: Double?
)