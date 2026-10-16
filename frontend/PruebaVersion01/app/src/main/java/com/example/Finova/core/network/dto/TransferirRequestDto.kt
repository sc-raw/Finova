package com.example.Finova.core.network.dto

import com.google.gson.annotations.SerializedName

data class TransferirRequestDto(
    @SerializedName("cuentaOrigenId") val cuentaOrigenId: Long,
    @SerializedName("cuentaDestinoId") val cuentaDestinoId: Long,
    @SerializedName("monto") val monto: Double,
    @SerializedName("descripcion") val descripcion: String?
)