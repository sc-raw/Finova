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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Finova.core.design.theme.*

// ============================================================
// FINOVA — Registro
// Vertical 1 · feature/auth
// ============================================================

@Composable
fun RegistroScreen(
    onNavigate: (String) -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }
    var aceptaTerminos by remember { mutableStateOf(false) }

    val cargando by viewModel.registroCargando.collectAsState()
    val error by viewModel.registroError.collectAsState()
    val registroExitoso by viewModel.registroExitoso.collectAsState()

    LaunchedEffect(registroExitoso) {
        if (registroExitoso) {
            viewModel.resetearEstados()
            onNavigate("login")
        }
    }

    val coinciden = confirmarContrasena.isNotEmpty() &&
            confirmarContrasena == contrasena &&
            confirmarContrasena.length >= 8

    val puedeEnviar = nombres.isNotBlank()
            && apellidos.isNotBlank()
            && correo.isNotBlank()
            && contrasena.length >= 8
            && coinciden
            && aceptaTerminos
            && !cargando

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FinovaBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // ---------- Top bar con back ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(FinovaSurfaceContainer)
                        .clickable { onNavigate("login") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = FinovaOnSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ---------- Header: Logo + Título ----------
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(FinovaPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "F",
                        style = FinovaDisplayLgMobile,
                        color = FinovaOnPrimary
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-6).dp, y = 6.dp)
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(FinovaPrimary)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Crea tu cuenta",
                    style = FinovaHeadlineLg,
                    color = FinovaOnSurface
                )
            }

            Spacer(Modifier.height(24.dp))

            // ---------- Card del formulario ----------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(FinovaSurfaceContainerLowest)
                    .padding(16.dp)
            ) {
                // Nombres
                CampoRegistro(
                    label = "Nombres",
                    valor = nombres,
                    onValorChange = { nombres = it },
                    placeholder = "Ej. Gerson Joel",
                    icono = Icons.Outlined.Person
                )

                Spacer(Modifier.height(14.dp))

                // Apellidos
                CampoRegistro(
                    label = "Apellidos",
                    valor = apellidos,
                    onValorChange = { apellidos = it },
                    placeholder = "Ej. Silva Carrera",
                    icono = Icons.Outlined.Person
                )

                Spacer(Modifier.height(14.dp))

                // Correo
                CampoRegistro(
                    label = "Correo electrónico",
                    valor = correo,
                    onValorChange = { correo = it },
                    placeholder = "andrea@correo.com",
                    icono = Icons.Outlined.Mail,
                    keyboardType = KeyboardType.Email
                )

                Spacer(Modifier.height(14.dp))

                // Contraseña
                CampoRegistro(
                    label = "Contraseña",
                    valor = contrasena,
                    onValorChange = { contrasena = it },
                    placeholder = "Mínimo 8 caracteres",
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

                Spacer(Modifier.height(14.dp))

                // Confirmar contraseña
                CampoRegistro(
                    label = "Confirmar contraseña",
                    valor = confirmarContrasena,
                    onValorChange = { confirmarContrasena = it },
                    placeholder = "Repite tu contraseña",
                    icono = Icons.Outlined.VerifiedUser,
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation(),
                    trailing = if (coinciden) {
                        {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Coinciden",
                                tint = FinovaSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else null
                )

                Spacer(Modifier.height(14.dp))

                // ---------- Checkbox términos ----------
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { aceptaTerminos = !aceptaTerminos },
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(20.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (aceptaTerminos) FinovaPrimary
                                else FinovaSurfaceContainerLow
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (aceptaTerminos) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = FinovaOnPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Acepto los Términos de servicio y la Política de privacidad.",
                        style = FinovaBodySm,
                        color = FinovaOnSurfaceVariant
                    )
                }

                // ---------- Mensaje de error ----------
                error?.let {
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        color = FinovaErrorContainer,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = it,
                            style = FinovaBodySm,
                            color = FinovaError,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ---------- Botón Crear cuenta ----------
                Button(
                    onClick = {
                        viewModel.registrar(
                            nombres = nombres.trim(),
                            apellidos = apellidos.trim(),
                            correo = correo.trim(),
                            password = contrasena
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FinovaPrimaryContainer,
                        contentColor = FinovaOnPrimary
                    ),
                    enabled = puedeEnviar
                ) {
                    if (cargando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = FinovaOnPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Crear cuenta",
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
            }

            Spacer(Modifier.height(20.dp))

            // ---------- ¿Ya tienes cuenta? ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Ya tienes cuenta?",
                    style = FinovaBodyMd,
                    color = FinovaOnSurfaceVariant
                )
                Spacer(Modifier.width(6.dp))
                Row(
                    modifier = Modifier
                        .clickable { onNavigate("login") }
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Iniciar sesión",
                        style = FinovaLabelLg,
                        color = FinovaPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = FinovaPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ============================================================
// Componente interno
// ============================================================

@Composable
private fun CampoRegistro(
    label: String,
    valor: String,
    onValorChange: (String) -> Unit,
    placeholder: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: (@Composable () -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = FinovaLabelLg,
            color = FinovaOnSurface
        )
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier
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
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(10.dp))
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
                                color = FinovaOutline.copy(alpha = 0.7f)
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
}