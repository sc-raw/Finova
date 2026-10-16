package com.example.Finova.core.network.dto

import com.google.gson.annotations.SerializedName

data class RegistrarGastoRequestDto(
    @SerializedName("cuentaId") val cuentaId: Long,
    @SerializedName("categoriaId") val categoriaId: Long,
    @SerializedName("metodoPagoId") val metodoPagoId: Long,
    @SerializedName("monto") val monto: Double,
    @SerializedName("descripcion") val descripcion: String?
)