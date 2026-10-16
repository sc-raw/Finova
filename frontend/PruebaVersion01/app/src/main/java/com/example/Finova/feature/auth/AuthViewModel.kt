package com.example.Finova.feature.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Finova.core.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()
    private val sessionManager = SessionManager(application)

    // ============ LOGIN ============
    private val _loginCargando = MutableStateFlow(false)
    val loginCargando: StateFlow<Boolean> = _loginCargando

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    private val _loginExitoso = MutableStateFlow(false)
    val loginExitoso: StateFlow<Boolean> = _loginExitoso

    // ============ REGISTRO ============
    private val _registroCargando = MutableStateFlow(false)
    val registroCargando: StateFlow<Boolean> = _registroCargando

    private val _registroError = MutableStateFlow<String?>(null)
    val registroError: StateFlow<String?> = _registroError

    private val _registroExitoso = MutableStateFlow(false)
    val registroExitoso: StateFlow<Boolean> = _registroExitoso

    // ============ MÉTODOS ============

    fun login(correo: String, password: String) {
        if (correo.isBlank() || password.isBlank()) {
            _loginError.value = "Correo y contraseña son obligatorios"
            return
        }

        viewModelScope.launch {
            _loginCargando.value = true
            _loginError.value = null

            try {
                val response = repository.login(correo.trim(), password)
                val usuario = response.usuario
                val token = response.token

                if (usuario != null && token != null) {
                    sessionManager.guardarSesion(
                        usuarioId = usuario.id,
                        nombres = usuario.nombres ?: "",
                        apellidos = usuario.apellidos ?: "",
                        correo = usuario.correo ?: "",
                        token = token
                    )
                    _loginExitoso.value = true
                } else {
                    _loginError.value = "Respuesta del servidor inválida"
                }
            } catch (e: Exception) {
                _loginError.value = traducirError(e.message)
            } finally {
                _loginCargando.value = false
            }
        }
    }

    fun registrar(
        nombres: String,
        apellidos: String,
        correo: String,
        password: String
    ) {
        if (nombres.isBlank() || apellidos.isBlank()
            || correo.isBlank() || password.isBlank()) {
            _registroError.value = "Todos los campos son obligatorios"
            return
        }

        if (password.length < 8) {
            _registroError.value = "La contraseña debe tener al menos 8 caracteres"
            return
        }

        viewModelScope.launch {
            _registroCargando.value = true
            _registroError.value = null

            try {
                repository.registrar(
                    nombres.trim(),
                    apellidos.trim(),
                    correo.trim(),
                    password
                )
                _registroExitoso.value = true
            } catch (e: Exception) {
                _registroError.value = traducirError(e.message)
            } finally {
                _registroCargando.value = false
            }
        }
    }

    fun resetearEstados() {
        _loginError.value = null
        _loginExitoso.value = false
        _registroError.value = null
        _registroExitoso.value = false
    }

    fun cerrarSesion() {
        sessionManager.cerrarSesion()
    }

    private fun traducirError(mensaje: String?): String {
        return when {
            mensaje == null -> "Error desconocido"
            mensaje.contains("401") -> "Correo o contraseña incorrectos"
            mensaje.contains("Connection refused") -> "No se pudo conectar al servidor"
            mensaje.contains("Unable to resolve host") -> "Sin conexión a Internet"
            else -> mensaje
        }
    }
}