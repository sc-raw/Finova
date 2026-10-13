package com.example.Finova.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.Finova.core.design.components.*
import com.example.Finova.core.design.theme.*

// ============================================================
// FINOVA — Login
// Vertical 1 · feature/auth
// Basado en Image 2.html — SOLO VISTA
// ============================================================

@Composable
fun LoginScreen(
    onNavigate: (String) -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FinovaBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(40.dp))

            // ---------- Logo ----------
            FinovaLogo()

            Spacer(Modifier.height(40.dp))

            // ---------- Card blanca con formulario ----------
            FinovaCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp,
                padding = 20.dp
            ) {
                // ---------- Correo ----------
                Text(
                    text = "Correo electrónico",
                    style = FinovaLabelMd,
                    color = FinovaOnSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                InputConIcono(
                    valor = correo,
                    onValorChange = { correo = it },
                    placeholder = "ejemplo@finova.app",
                    icono = Icons.Outlined.Mail,
                    keyboardType = KeyboardType.Email
                )

                Spacer(Modifier.height(16.dp))

                // ---------- Contraseña ----------
                Text(
                    text = "Contraseña",
                    style = FinovaLabelMd,
                    color = FinovaOnSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                InputConIcono(
                    valor = contrasena,
                    onValorChange = { contrasena = it },
                    placeholder = "••••••••",
                    icono = Icons.Outlined.Lock,
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (mostrarContrasena)
                        VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailing = {
                        Icon(
                            imageVector = if (mostrarContrasena)
                                Icons.Outlined.VisibilityOff
                            else Icons.Outlined.Visibility,
                            contentDescription = "Mostrar contraseña",
                            tint = FinovaOutline,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { mostrarContrasena = !mostrarContrasena }
                        )
                    }
                )

                Spacer(Modifier.height(8.dp))

                // ---------- Olvidé contraseña ----------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        style = FinovaLabelMd,
                        color = FinovaPrimary,
                        modifier = Modifier
                            .clickable { /* TODO */ }
                            .padding(vertical = 4.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // ---------- Botón Iniciar sesión ----------
                Button(
                    onClick = { onNavigate("dashboard") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FinovaPrimary,
                        contentColor = FinovaOnPrimary
                    )
                ) {
                    Text(
                        text = "Iniciar sesión",
                        style = FinovaLabelLg
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ---------- ¿No tienes cuenta? ----------
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¿No tienes una cuenta?",
                    style = FinovaBodyMd,
                    color = FinovaOnSurfaceVariant
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Crear cuenta",
                    style = FinovaLabelLg,
                    color = FinovaPrimary,
                    modifier = Modifier
                        .clickable { onNavigate("registro") }
                        .padding(4.dp)
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ============================================================
// Componentes compartidos
// ============================================================

@Composable
fun FinovaLogo(
    size: Int = 56,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape((size * 0.4f).dp))
            .background(FinovaPrimaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "F",
            style = FinovaDisplayLgMobile,
            color = FinovaOnPrimary
        )
        // Punto decorativo arriba a la derecha
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-8).dp, y = 8.dp)
                .size(10.dp)
                .clip(CircleShape)
                .background(FinovaSecondaryFixed)
        )
    }
}

@Composable
fun InputConIcono(
    valor: String,
    onValorChange: (String) -> Unit,
    placeholder: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(FinovaSurfaceContainerLow)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = FinovaOutline,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))

        BasicTextField(
            value = valor,
            onValueChange = onValorChange,
            modifier = Modifier.weight(1f),
            textStyle = FinovaBodyMd.copy(color = FinovaOnSurface),
            singleLine = true,
            cursorBrush = SolidColor(FinovaPrimary),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            decorationBox = { inner ->
                Box {
                    if (valor.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = FinovaBodyMd,
                            color = FinovaOutlineVariant
                        )
                    }
                    inner()
                }
            }
        )

        if (trailing != null) {
            Spacer(Modifier.width(8.dp))
            trailing()
        }
    }
}