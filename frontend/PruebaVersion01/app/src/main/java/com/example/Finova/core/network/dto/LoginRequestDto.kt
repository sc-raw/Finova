package com.example.Finova.core.network.dto

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("correo") val correo: String,
    @SerializedName("password") val password: String
)