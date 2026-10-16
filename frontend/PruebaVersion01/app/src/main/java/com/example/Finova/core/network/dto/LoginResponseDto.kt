package com.example.Finova.core.network.dto

import com.google.gson.annotations.SerializedName

data class LoginResponseDto(
    @SerializedName("token") val token: String?,
    @SerializedName("tipo") val tipo: String?,
    @SerializedName("usuario") val usuario: UsuarioDto?
)