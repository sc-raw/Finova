package com.example.Finova.core.network.dto

import com.google.gson.annotations.SerializedName

data class ComprobanteDto(
    @SerializedName("id") val id: Long,
    @SerializedName("movimiento") val movimiento: MovimientoDto?,
    @SerializedName("nombreComercio") val nombreComercio: String?,
    @SerializedName("ruc") val ruc: String?,
    @SerializedName("codigoOperacion") val codigoOperacion: String?,
    @SerializedName("rutaComprobante") val rutaComprobante: String?
)