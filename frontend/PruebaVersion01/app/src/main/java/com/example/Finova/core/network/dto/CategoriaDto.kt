package com.example.Finova.core.network.dto

import com.google.gson.annotations.SerializedName

data class CategoriaDto(
    @SerializedName("id") val id: Long,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("tipo") val tipo: String?  // "GASTO" o "INGRESO"
)