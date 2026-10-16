package com.example.Finova.core.network.dto

import com.google.gson.annotations.SerializedName

data class MetaDto(
    @SerializedName("id") val id: Long,
    @SerializedName("usuarioId") val usuarioId: Long?,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("montoObjetivo") val montoObjetivo: Double?,
    @SerializedName("montoActual") val montoActual: Double?,
    @SerializedName("fechaObjetivo") val fechaObjetivo: String? // "yyyy-MM-dd"
)