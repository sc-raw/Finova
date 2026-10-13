package com.example.Finova.core.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.Finova.core.design.theme.FinovaErrorContainer
import com.example.Finova.core.design.theme.FinovaExpenseRed
import com.example.Finova.core.design.theme.FinovaIncomeGreen
import com.example.Finova.core.design.theme.FinovaSecondaryFixed

// ============================================================
// FINOVA — Extensions
// Helpers de uso común: colores por monto, modifiers condicionales, etc.
// ============================================================

// ---------- Color semántico por monto ----------

/**
 * Devuelve el color semántico según sea ingreso o egreso.
 * Usa los colores del DESIGN.md.
 */
fun colorParaMonto(esIngreso: Boolean): Color =
    if (esIngreso) FinovaIncomeGreen else FinovaExpenseRed

/**
 * Devuelve el color del fondo del icono según sea ingreso o egreso.
 */
fun colorFondoParaMonto(esIngreso: Boolean): Color =
    if (esIngreso) FinovaSecondaryFixed.copy(alpha = 0.5f)
    else FinovaErrorContainer.copy(alpha = 0.6f)

// ---------- Modifier condicional ----------

/**
 * Aplica un modifier solo si la condición es verdadera.
 * Uso: Modifier.ifTrue(esActivo) { background(Color.Red) }
 */
inline fun Modifier.ifTrue(
    condicion: Boolean,
    block: Modifier.() -> Modifier
): Modifier = if (condicion) this.block() else this

/**
 * Aplica un modifier si la condición es falsa.
 */
inline fun Modifier.ifFalse(
    condicion: Boolean,
    block: Modifier.() -> Modifier
): Modifier = if (!condicion) this.block() else this

// ---------- Visibilidad ----------

/**
 * Oculta o muestra un composable según un booleano.
 */
fun Modifier.visible(visible: Boolean): Modifier =
    this.alpha(if (visible) 1f else 0f)

/**
 * Alterna la opacidad de un composable.
 */
fun Modifier.setVisible(visible: Boolean, alpha: Float = 0.5f): Modifier =
    if (visible) this else this.alpha(alpha)

// ---------- Texto con énfasis ----------

/**
 * Crea un texto donde una parte va resaltada.
 * Ej: highlightedText("Te quedan ", "S/ 1,420", " de S/ 5,500")
 */
fun highlightedText(
    inicio: String,
    resaltado: String,
    fin: String,
    colorResaltado: Color
): AnnotatedString = buildAnnotatedString {
    append(inicio)
    withStyle(SpanStyle(color = colorResaltado)) {
        append(resaltado)
    }
    append(fin)
}

// ---------- String helpers ----------

/**
 * Toma los últimos 4 caracteres de un string.
 */
fun String.ultimos4(): String =
    if (length >= 4) takeLast(4) else this

/**
 * Capitaliza la primera letra.
 */
fun String.capitalizeFirst(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

/**
 * Enmascara el string como contraseña: "••••••"
 */
fun String.toMasked(maskChar: Char = '•'): String =
    maskChar.toString().repeat(length.coerceAtLeast(6))

// ---------- Doble helpers ----------

/**
 * Redondea a 2 decimales (útil para montos).
 */
fun Double.redondear2(): Double = kotlin.math.round(this * 100) / 100.0

/**
 * Convierte el monto a un string de 2 decimales sin formato: "142.50"
 */
fun Double.aStringPlano(): String = "%.2f".format(this)