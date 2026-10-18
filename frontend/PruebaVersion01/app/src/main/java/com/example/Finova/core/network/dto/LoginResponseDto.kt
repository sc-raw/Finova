package com.example.Finova.core.network.dto

import com.google.gson.annotations.SerializedName

data class LoginResponseDto(
    //Despues
    @SerializedName("id")
    val id: Long?,

    @SerializedName("nombres")
    val nombres: String?,

    @SerializedName("correo")
    val correo: String?,

    @SerializedName("token")
    val token: String?


)

    //Antes
   //@SerializedName("token") val token: String?,
   //@SerializedName("tipo") val tipo: String?,
   //@SerializedName("usuario") val usuario: UsuarioDto?
