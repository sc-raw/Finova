package com.example.Finova.core.network.dto

data class EditarMovimientoRequest(
    val titulo: String,
    val monto: Double,
    val categoriaId: Long
)
