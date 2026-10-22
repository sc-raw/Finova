package com.example.Finova.feature.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Finova.core.design.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CambiarPasswordScreen(
    onBack: () -> Unit,
    viewModel: PerfilViewModel = viewModel()
) {
    var actual by remember { mutableStateOf("") }
    var nueva by remember { mutableStateOf("") }
    var repetir by remember { mutableStateOf("") }
    var mostrarActual by remember { mutableStateOf(false) }
    var mostrarNueva by remember { mutableStateOf(false) }
    var mostrarRepetir by remember { mutableStateOf(false) }
    var errorLocal by remember { mutableStateOf<String?>(null) }
    val cargando by viewModel.cargando.collectAsState()
    val errorServidor by viewModel.error.collectAsState()
    val exito by viewModel.exitoPassword.collectAsState()

    LaunchedEffect(Unit) { viewModel.resetearEstadoPassword() }
    if (exito) {
        AlertDialog(
            onDismissRequest = { viewModel.resetearEstadoPassword(); onBack() },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FinovaPrimary) },
            title = { Text("Contraseña actualizada") },
            text = { Text("Tu contraseña se cambió correctamente.") },
            confirmButton = { TextButton(onClick = { viewModel.resetearEstadoPassword(); onBack() }) { Text("Aceptar") } }
        )
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(color = FinovaSurface, shadowElevation = 1.dp) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = FinovaOnSurface) }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("FINOVA", style = FinovaLabelSm, color = FinovaPrimary, fontWeight = FontWeight.SemiBold)
                        Text("Protege tu cuenta", style = FinovaHeadlineSm, color = FinovaOnSurface)
                    }
                }
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Surface(color = FinovaSurfaceContainerLowest, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.size(52.dp).background(FinovaSurfaceContainerLow, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.LockReset, contentDescription = null, tint = FinovaPrimary, modifier = Modifier.size(28.dp))
                    }
                    Text("Protege tu cuenta", style = FinovaHeadlineSm, color = FinovaOnSurface)
                    Text("Ingresa tu contraseña actual y crea una nueva de al menos 8 caracteres.", style = FinovaBodyMd, color = FinovaOnSurfaceVariant)
                    PasswordField("Contraseña actual", actual, { actual = it }, mostrarActual, { mostrarActual = !mostrarActual })
                    PasswordField("Nueva contraseña", nueva, { nueva = it }, mostrarNueva, { mostrarNueva = !mostrarNueva })
                    PasswordField("Repetir nueva contraseña", repetir, { repetir = it }, mostrarRepetir, { mostrarRepetir = !mostrarRepetir })
                    if (nueva.isNotEmpty() && nueva.length < 8) Text("La nueva contraseña debe tener al menos 8 caracteres.", style = FinovaBodySm, color = FinovaError)
                    else if (repetir.isNotEmpty() && nueva != repetir) Text("Las contraseñas nuevas no coinciden.", style = FinovaBodySm, color = FinovaError)
                    (errorLocal ?: errorServidor)?.let { msg ->
                        Surface(color = FinovaErrorContainer, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                            Text(msg, color = FinovaError, style = FinovaBodySm, modifier = Modifier.padding(10.dp))
                        }
                    }
                    Button(
                        onClick = {
                            errorLocal = when {
                                actual.isBlank() -> "Ingresa tu contraseña actual."
                                nueva.length < 8 -> "La nueva contraseña debe tener al menos 8 caracteres."
                                nueva != repetir -> "Las contraseñas nuevas no coinciden."
                                actual == nueva -> "La nueva contraseña debe ser diferente de la actual."
                                else -> null
                            }
                            if (errorLocal == null) viewModel.cambiarPassword(actual, nueva)
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        enabled = !cargando,
                        colors = ButtonDefaults.buttonColors(containerColor = FinovaPrimaryContainer, contentColor = FinovaOnPrimary)
                    ) {
                        if (cargando) CircularProgressIndicator(Modifier.size(20.dp), color = FinovaOnPrimary, strokeWidth = 2.dp)
                        else { Icon(Icons.Default.Save, contentDescription = null); Spacer(Modifier.width(8.dp)); Text("Guardar nueva contraseña", fontWeight = FontWeight.SemiBold) }
                    }
                    OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(14.dp), enabled = !cargando) { Text("Cancelar") }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = FinovaOnSurfaceVariant, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text("Tu contraseña se valida de forma segura en Finova.", style = FinovaBodySm, color = FinovaOnSurfaceVariant)
            }
        }
    }
}

@Composable
private fun PasswordField(label: String, value: String, onValueChange: (String) -> Unit, visible: Boolean, onToggleVisibility: () -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        trailingIcon = { TextButton(onClick = onToggleVisibility) { Text(if (visible) "Ocultar" else "Ver") } },
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        shape = RoundedCornerShape(12.dp)
    )
}
