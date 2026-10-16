package com.example.Finova.core.network.dto

import com.google.gson.annotations.SerializedName

data class InterpretarComprobanteResponseDto(
    @SerializedName("razonamiento") val razonamiento: String?,
    @SerializedName("tipoDocumento") val tipoDocumento: String?,
    @SerializedName("comercio") val comercio: String?,
    @SerializedName("ruc") val ruc: String?,
    @SerializedName("fecha") val fecha: String?,
    @SerializedName("moneda") val moneda: String?,
    @SerializedName("subtotal") val subtotal: Double?,
    @SerializedName("igv") val igv: Double?,
    @SerializedName("total") val total: Double?,
    @SerializedName("categoriaSugerida") val categoriaSugerida: String?,
    @SerializedName("confianza") val confianza: Double?,
    @SerializedName("items") val items: List<ItemDto>?,
    @SerializedName("rutaImagen") val rutaImagen: String?
) {
    data class ItemDto(
        @SerializedName("nombre") val nombre: String?,
        @SerializedName("cantidad") val cantidad: Int?,
        @SerializedName("precio") val precio: Double?
    )
}