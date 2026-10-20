package com.example.Finova.feature.perfil


import com.example.Finova.core.network.ApiClient
import com.example.Finova.core.network.dto.CambiarPasswordRequestDto
import com.example.Finova.core.network.dto.UsuarioDto

class PerfilRepository {

    suspend fun obtenerPerfil(usuarioId: Long): UsuarioDto {
        return ApiClient.apiService.obtenerUsuario(usuarioId)
    }

    suspend fun actualizarPerfil(usuarioId: Long, usuario: UsuarioDto): UsuarioDto {
        return ApiClient.apiService.actualizarUsuario(usuarioId, usuario)
    }

    suspend fun cambiarPassword(usuarioId: Long, request: CambiarPasswordRequestDto) {
        ApiClient.apiService.cambiarPassword(usuarioId, request)
    }
}