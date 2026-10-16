package com.example.Finova.core.network.dto

import com.google.gson.annotations.SerializedName

data class MovimientoDto(
    @SerializedName("id") val id: Long,
    @SerializedName("cuentaOrigenId") val cuentaOrigenId: Long?,
    @SerializedName("cuentaOrigenNombre") val cuentaOrigenNombre: String?,
    @SerializedName("cuentaDestinoId") val cuentaDestinoId: Long?,
    @SerializedName("cuentaDestinoNombre") val cuentaDestinoNombre: String?,
    @SerializedName("metaId") val metaId: Long?,
    @SerializedName("metaNombre") val metaNombre: String?,
    @SerializedName("categoriaId") val categoriaId: Long?,
    @SerializedName("categoriaNombre") val categoriaNombre: String?,
    @SerializedName("categoriaTipo") val categoriaTipo: String?,
    @SerializedName("metodoPagoId") val metodoPagoId: Long?,
    @SerializedName("metodoPagoNombre") val metodoPagoNombre: String?,
    @SerializedName("monto") val monto: Double?,
    @SerializedName("fechaMovimiento") val fechaMovimiento: String?,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("tipoMovimiento") val tipoMovimiento: String? // INGRESO, GASTO, TRANSFERENCIA, APORTE_META, RETIRO_META
)