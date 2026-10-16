package com.example.Finova.core.network.dto

import com.google.gson.annotations.SerializedName

data class CambiarPasswordRequestDto(
    @SerializedName("passwordActual") val passwordActual: String,
    @SerializedName("passwordNueva") val passwordNueva: String
)