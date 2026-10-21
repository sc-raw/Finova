package com.example.Finova.core.network.dto

import com.google.gson.annotations.SerializedName

data class RegistrarIngresoRequestDto(
    @SerializedName("cuentaId") val cuentaId: Long,
    @SerializedName("categoriaId") val categoriaId: Long,
    @SerializedName("metodoPagoId") val metodoPagoId: Long,
    @SerializedName("monto") val monto: Double,
    @SerializedName("titulo") val titulo: String?,
    @SerializedName("descripcion") val descripcion: String?
)