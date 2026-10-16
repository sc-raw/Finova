package com.example.Finova.core.network.dto

import com.google.gson.annotations.SerializedName

data class InterpretarComprobanteRequestDto(
    @SerializedName("ocrText") val ocrText: String
)