package com.example.Finova.core.network.dto

import com.google.gson.annotations.SerializedName

data class ErrorResponseDto(
    @SerializedName("timestamp") val timestamp: String?,
    @SerializedName("status") val status: Int?,
    @SerializedName("error") val error: String?,
    @SerializedName("mensaje") val mensaje: String?
)