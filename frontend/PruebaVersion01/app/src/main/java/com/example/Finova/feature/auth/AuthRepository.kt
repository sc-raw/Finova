package com.example.Finova.feature.auth

import com.example.Finova.core.network.ApiClient
import com.example.Finova.core.network.dto.LoginRequestDto
import com.example.Finova.core.network.dto.LoginResponseDto
import com.example.Finova.core.network.dto.RegistroRequestDto
import com.example.Finova.core.network.dto.UsuarioDto

class AuthRepository {

    suspend fun login(correo: String, password: String): LoginResponseDto {
        return ApiClient.apiService.login(
            LoginRequestDto(correo = correo, password = password)
        )
    }

    suspend fun registrar(
        nombres: String,
        apellidos: String,
        correo: String,
        password: String
    ): UsuarioDto {
        return ApiClient.apiService.registrar(
            RegistroRequestDto(
                nombres = nombres,
                apellidos = apellidos,
                correo = correo,
                password = password
            )
        )
    }
}