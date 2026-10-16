package com.example.Finova.core.network.dto

import com.google.gson.annotations.SerializedName

data class MetodoPagoDto(
    @SerializedName("id") val id: Long,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("activo") val activo: Boolean?
)