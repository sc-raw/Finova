package com.example.Finova.feature.perfil

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Finova.core.network.dto.UsuarioDto
import com.example.Finova.core.network.dto.CambiarPasswordRequestDto
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

    private val _exitoPassword = MutableStateFlow(false)
    val exitoPassword: StateFlow<Boolean> = _exitoPassword

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
                    // El correo se conserva: no es editable desde Mis datos.
                    correo = usuarioActual?.correo ?: correo,
                    foto = usuarioActual?.foto,
                    fechaCreacion = usuarioActual?.fechaCreacion
                )

                repository.actualizarPerfil(usuarioId, usuarioActualizado)

                // Actualizamos el estado local y también el SessionManager por si acaso
                sessionManager.guardarSesion(
                    usuarioId = usuarioId,
                    nombres = nombres.trim(),
                    apellidos = apellidos.trim(),
                    correo = usuarioActualizado.correo.orEmpty(),
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

    fun cambiarPassword(passwordActual: String, passwordNueva: String) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            _exitoPassword.value = false
            try {
                val usuarioId = sessionManager.obtenerUsuarioId()
                if (usuarioId == -1L) {
                    _error.value = "No se encontró la sesión. Inicia sesión nuevamente."
                    return@launch
                }
                if (passwordActual.isBlank()) {
                    _error.value = "Ingresa tu contraseña actual."
                    return@launch
                }
                if (passwordNueva.length < 8) {
                    _error.value = "La nueva contraseña debe tener al menos 8 caracteres."
                    return@launch
                }
                if (passwordActual == passwordNueva) {
                    _error.value = "La nueva contraseña debe ser diferente de la actual."
                    return@launch
                }
                repository.cambiarPassword(
                    usuarioId,
                    CambiarPasswordRequestDto(
                        passwordActual = passwordActual,
                        passwordNueva = passwordNueva
                    )
                )
                _exitoPassword.value = true
            } catch (e: Exception) {
                _error.value = when {
                    e.message?.contains("401") == true || e.message?.contains("403") == true -> "No autorizado. Inicia sesión nuevamente."
                    e.message?.contains("400") == true -> "La contraseña actual es incorrecta o los datos no son válidos."
                    else -> "No se pudo cambiar la contraseña. Verifica los datos e inténtalo nuevamente."
                }
            } finally {
                _cargando.value = false
            }
        }
    }

    fun resetearEstadoPassword() {
        _error.value = null
        _exitoPassword.value = false
    }
}