package com.example.Finova.core.network.dto

import com.google.gson.annotations.SerializedName

data class ConfirmarComprobanteRequestDto(
    @SerializedName("tipoMovimiento") val tipoMovimiento: String, // GASTO, INGRESO, etc.
    @SerializedName("cuentaOrigenId") val cuentaOrigenId: Long?,
    @SerializedName("cuentaDestinoId") val cuentaDestinoId: Long?,
    @SerializedName("categoriaId") val categoriaId: Long?,
    @SerializedName("metodoPagoId") val metodoPagoId: Long?,
    @SerializedName("monto") val monto: Double?,
    @SerializedName("fechaMovimiento") val fechaMovimiento: String?,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("nombreComercio") val nombreComercio: String?,
    @SerializedName("ruc") val ruc: String?,
    @SerializedName("codigoOperacion") val codigoOperacion: String?,
    @SerializedName("rutaComprobante") val rutaComprobante: String?
)