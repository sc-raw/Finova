package com.example.Finova.feature.perfil

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Finova.core.network.dto.UsuarioDto
import com.example.Finova.core.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PerfilRepository()
    private val sessionManager = SessionManager(application)

    private val _usuario = MutableStateFlow<UsuarioDto?>(null)
    val usuario: StateFlow<UsuarioDto?> = _usuario

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _exitoActualizacion = MutableStateFlow(false)
    val exitoActualizacion: StateFlow<Boolean> = _exitoActualizacion

    fun cargarPerfil() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            try {
                val usuarioId = sessionManager.obtenerUsuarioId()

                // Tu SessionManager devuelve -1L si no hay sesión
                if (usuarioId == -1L) {
                    _error.value = "No se encontró la sesión del usuario. Inicia sesión nuevamente."
                    _cargando.value = false
                    return@launch
                }

                _usuario.value = repository.obtenerPerfil(usuarioId)

            } catch (e: Exception) {
                _error.value = "Error al cargar los datos: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun guardarCambios(nombres: String, apellidos: String, correo: String) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            try {
                val usuarioId = sessionManager.obtenerUsuarioId()
                if (usuarioId == -1L) return@launch

                val usuarioActual = _usuario.value

                val usuarioActualizado = UsuarioDto(
                    id = usuarioId,
                    nombres = nombres.trim(),
                    apellidos = apellidos.trim(),
                    correo = correo.trim(),
                    foto = usuarioActual?.foto,
                    fechaCreacion = usuarioActual?.fechaCreacion
                )

                repository.actualizarPerfil(usuarioId, usuarioActualizado)

                // Actualizamos el estado local y también el SessionManager por si acaso
                sessionManager.guardarSesion(
                    usuarioId = usuarioId,
                    nombres = nombres.trim(),
                    apellidos = apellidos.trim(),
                    correo = correo.trim(),
                    token = sessionManager.obtenerToken()
                )

                _usuario.value = usuarioActualizado
                _exitoActualizacion.value = true

            } catch (e: Exception) {
                _error.value = "Error al guardar: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun resetearEstados() {
        _error.value = null
        _exitoActualizacion.value = false
    }
}