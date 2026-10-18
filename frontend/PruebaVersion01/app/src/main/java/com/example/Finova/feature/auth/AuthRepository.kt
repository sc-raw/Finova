package com.example.Finova.feature.auth

import com.example.Finova.core.network.ApiClient
import com.example.Finova.core.network.dto.LoginRequestDto
import com.example.Finova.core.network.dto.LoginResponseDto
import com.example.Finova.core.network.dto.RegistroRequestDto
import com.example.Finova.core.network.dto.UsuarioDto

class AuthRepository {

    // 1. Aseguramos que llame a .login() pasándole el DTO correcto
    suspend fun login(correo: String, password: String): LoginResponseDto {
        val request = LoginRequestDto(correo = correo, password = password)
        return ApiClient.apiService.login(request)
    }

    // 2. Aseguramos que llame a .registrar() pasándole el DTO correcto
    suspend fun registrar(
        nombres: String,
        apellidos: String,
        correo: String,
        password: String
    ): UsuarioDto {
        val request = RegistroRequestDto(
            nombres = nombres,
            apellidos = apellidos,
            correo = correo,
            password = password
        )
        return ApiClient.apiService.registrar(request)
    }
}
