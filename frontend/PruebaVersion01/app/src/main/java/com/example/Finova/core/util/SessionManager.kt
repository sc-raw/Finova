package com.example.Finova.core.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "finova_session",
        Context.MODE_PRIVATE
    )

    fun guardarSesion(
        usuarioId: Long,
        nombres: String,
        apellidos: String,
        correo: String,
        token: String
    ) {
        prefs.edit().apply {
            putLong("usuario_id", usuarioId)
            putString("nombres", nombres)
            putString("apellidos", apellidos)
            putString("correo", correo)
            putString("token", token)
            apply()
        }
    }

    fun obtenerUsuarioId(): Long = prefs.getLong("usuario_id", -1L)
    fun obtenerNombres(): String = prefs.getString("nombres", "") ?: ""
    fun obtenerApellidos(): String = prefs.getString("apellidos", "") ?: ""
    fun obtenerCorreo(): String = prefs.getString("correo", "") ?: ""
    fun obtenerToken(): String = prefs.getString("token", "") ?: ""

    fun estaLogueado(): Boolean = obtenerUsuarioId() != -1L

    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }
}