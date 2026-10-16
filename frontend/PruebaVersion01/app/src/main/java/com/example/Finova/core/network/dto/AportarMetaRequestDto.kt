package com.example.Finova.core.network.dto

import com.google.gson.annotations.SerializedName

data class AportarMetaRequestDto(
    @SerializedName("cuentaId") val cuentaId: Long,
    @SerializedName("metaId") val metaId: Long,
    @SerializedName("monto") val monto: Double,
    @SerializedName("descripcion") val descripcion: String?
)