package com.example.Finova.core.network.dto

import com.google.gson.annotations.SerializedName

data class UsuarioDto(
    @SerializedName("id") val id: Long,
    @SerializedName("nombres") val nombres: String?,
    @SerializedName("apellidos") val apellidos: String?,
    @SerializedName("correo") val correo: String?,
    @SerializedName("foto") val foto: String?,
    @SerializedName("fechaCreacion") val fechaCreacion: String?
)