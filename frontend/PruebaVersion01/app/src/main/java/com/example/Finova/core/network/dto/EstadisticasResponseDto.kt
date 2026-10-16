package com.example.Finova.core.network.dto

import com.google.gson.annotations.SerializedName

data class EstadisticasResponseDto(
    @SerializedName("totalIngresos") val totalIngresos: Double?,
    @SerializedName("totalGastos") val totalGastos: Double?,
    @SerializedName("tasaAhorro") val tasaAhorro: Double?,
    @SerializedName("ingresosAnterior") val ingresosAnterior: Double?,
    @SerializedName("gastosAnterior") val gastosAnterior: Double?,
    @SerializedName("distribucionCategorias") val distribucionCategorias: List<CategoriaTotalDto>?,
    @SerializedName("evolucionMensual") val evolucionMensual: List<EvolucionMensualDto>?
) {
    data class CategoriaTotalDto(
        @SerializedName("nombre") val nombre: String?,
        @SerializedName("color") val color: String?,
        @SerializedName("icon") val icon: String?,
        @SerializedName("total") val total: Double?,
        @SerializedName("porcentaje") val porcentaje: Double?
    )

    data class EvolucionMensualDto(
        @SerializedName("mes") val mes: String?,
        @SerializedName("ingresos") val ingresos: Double?,
        @SerializedName("gastos") val gastos: Double?
    )
}