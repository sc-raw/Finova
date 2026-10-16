package com.example.Finova.core.network.dto

import com.google.gson.annotations.SerializedName

data class RegistroRequestDto(
    @SerializedName("nombres") val nombres: String,
    @SerializedName("apellidos") val apellidos: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("password") val password: String
)