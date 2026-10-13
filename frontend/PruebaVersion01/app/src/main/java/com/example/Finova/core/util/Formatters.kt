package com.example.Finova.core.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ============================================================
// FINOVA — Formatters
// Formato de moneda, números y fechas (es-PE)
// ============================================================

private val localePE = Locale("es", "PE")

// ---------- Moneda ----------

/**
 * Formatea un monto como moneda peruana: "S/ 8,450.20"
 */
fun formatSoles(monto: Double): String {
    val nf = NumberFormat.getNumberInstance(localePE).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return "S/ ${nf.format(monto)}"
}

/**
 * Formatea un monto en dólares: "$ 2,253.38"
 */
fun formatDolares(monto: Double): String {
    val nf = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return "$ ${nf.format(monto)}"
}

/**
 * Formatea un monto sin símbolo de moneda, solo el número con separadores:
 * "8,450.20"
 */
fun formatNumber(monto: Double): String {
    val nf = NumberFormat.getNumberInstance(localePE).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return nf.format(monto)
}

/**
 * Formatea un monto con signo (+/-) según sea ingreso o egreso.
 * Ej: "+S/ 3,800.00" o "-S/ 142.50"
 */
fun formatSignedSoles(monto: Double, esIngreso: Boolean): String {
    val signo = if (esIngreso) "+" else "-"
    return "$signo${formatSoles(kotlin.math.abs(monto))}"
}

/**
 * Formatea un monto en porcentaje simple: "74%"
 */
fun formatPorcentaje(valor: Int): String = "$valor%"

/**
 * Formatea el tipo de cambio: "3.75"
 */
fun formatTipoCambio(tc: Double): String = "%.2f".format(tc)

// ---------- Números de cuenta ----------

/**
 * Enmascara un número de cuenta dejando los últimos 4 dígitos visibles.
 * Ej: "12345678904821" -> "•••• 4821"
 */
fun maskAccountNumber(numero: String): String {
    val limpio = numero.filter { it.isDigit() }
    return if (limpio.length >= 4) {
        "•••• ${limpio.takeLast(4)}"
    } else {
        "••••"
    }
}

/**
 * Formatea tarjeta en grupos: "4821 •• 9012"
 * (por si luego lo necesitas)
 */
fun maskCardNumber(numero: String): String {
    val limpio = numero.filter { it.isDigit() }
    return if (limpio.length >= 8) {
        "${limpio.take(4)} •• ${limpio.takeLast(4)}"
    } else {
        maskAccountNumber(numero)
    }
}

// ---------- Fechas ----------

/**
 * Formatea una fecha como "Hoy, 13:45", "Ayer, 09:00" o "18 Feb".
 */
fun formatFechaRelativa(fecha: Date): String {
    val ahora = Calendar.getInstance()
    val ayer = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    val fechaCal = Calendar.getInstance().apply { time = fecha }

    val mismoAnio = ahora.get(Calendar.YEAR) == fechaCal.get(Calendar.YEAR)
    val mismoDia = ahora.get(Calendar.DAY_OF_YEAR) == fechaCal.get(Calendar.DAY_OF_YEAR)
    val esAyer = ayer.get(Calendar.DAY_OF_YEAR) == fechaCal.get(Calendar.DAY_OF_YEAR)
            && ayer.get(Calendar.YEAR) == fechaCal.get(Calendar.YEAR)

    val horaFmt = SimpleDateFormat("HH:mm", localePE)

    return when {
        mismoDia && mismoAnio -> "Hoy, ${horaFmt.format(fecha)}"
        esAyer && mismoAnio   -> "Ayer, ${horaFmt.format(fecha)}"
        else -> {
            val diaFmt = SimpleDateFormat("d MMM", localePE)
            diaFmt.format(fecha)
        }
    }
}

/**
 * Formatea el mes actual en español: "Marzo 2025"
 */
fun formatMesActual(): String {
    val fmt = SimpleDateFormat("MMMM yyyy", localePE)
    val mes = fmt.format(Date())
    return mes.replaceFirstChar { it.uppercase() }
}

/**
 * Formatea una fecha completa: "18 Feb 2025"
 */
fun formatFechaCorta(fecha: Date): String {
    val fmt = SimpleDateFormat("d MMM yyyy", localePE)
    return fmt.format(fecha)
}

/**
 * Formatea solo la hora: "13:45"
 */
fun formatHora(fecha: Date): String {
    val fmt = SimpleDateFormat("HH:mm", localePE)
    return fmt.format(fecha)
}