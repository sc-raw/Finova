package com.example.Finova.feature.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Finova.core.design.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisDatosScreen(
    onBack: () -> Unit,
    viewModel: PerfilViewModel = viewModel()
) {
    val usuario by viewModel.usuario.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()
    val exito by viewModel.exitoActualizacion.collectAsState()

    var mostrarModalEditar by remember { mutableStateOf(false) }
    var nombreEditado by remember { mutableStateOf("") }
    var apellidosEditados by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { if (usuario == null) viewModel.cargarPerfil() }
    LaunchedEffect(usuario) {
        nombreEditado = usuario?.nombres.orEmpty()
        apellidosEditados = usuario?.apellidos.orEmpty()
    }
    LaunchedEffect(exito) {
        if (exito) {
            mostrarModalEditar = false
            viewModel.resetearEstados()
        }
    }

    if (mostrarModalEditar) {
        AlertDialog(
            onDismissRequest = { if (!cargando) mostrarModalEditar = false },
            title = { Text("Editar datos personales") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = nombreEditado,
                        onValueChange = { nombreEditado = it },
                        label = { Text("Nombres") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = apellidosEditados,
                        onValueChange = { apellidosEditados = it },
                        label = { Text("Apellidos") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("El correo electrónico no se puede editar.", style = FinovaBodySm, color = FinovaOnSurfaceVariant)
                    error?.let { Text(it, color = FinovaError, style = FinovaBodySm) }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !cargando && nombreEditado.isNotBlank() && apellidosEditados.isNotBlank(),
                    onClick = {
                        viewModel.guardarCambios(
                            nombres = nombreEditado,
                            apellidos = apellidosEditados,
                            correo = usuario?.correo.orEmpty()
                        )
                    }
                ) {
                    if (cargando) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    else Text("Guardar cambios")
                }
            },
            dismissButton = {
                TextButton(enabled = !cargando, onClick = { mostrarModalEditar = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(color = FinovaSurface, shadowElevation = 1.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = FinovaOnSurface) }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("FINOVA", style = FinovaLabelSm, color = FinovaPrimary, fontWeight = FontWeight.SemiBold)
                        Text("Mis datos", style = FinovaHeadlineSm, color = FinovaOnSurface)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(FinovaSurfaceContainerLowest).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Información personal", style = FinovaLabelLg, color = FinovaOnSurface, fontWeight = FontWeight.SemiBold)
                    Text("ID: #${usuario?.id ?: "----"}", style = FinovaLabelSm, color = FinovaOnSurfaceVariant)
                }
                if (cargando && usuario == null) {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else {
                    DatoPersonal("Nombres", usuario?.nombres.orEmpty(), Icons.Default.Person)
                    DatoPersonal("Apellidos", usuario?.apellidos.orEmpty(), Icons.Default.Badge)
                    DatoPersonal("Correo electrónico", usuario?.correo.orEmpty(), Icons.Default.MailOutline, protegido = true)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = FinovaOnSurfaceVariant, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("El correo electrónico es de solo lectura.", style = FinovaBodySm, color = FinovaOnSurfaceVariant)
                    }
                }
                error?.let {
                    Surface(color = FinovaErrorContainer, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Text(it, color = FinovaError, style = FinovaBodySm, modifier = Modifier.padding(10.dp))
                    }
                }
                Button(
                    onClick = {
                        nombreEditado = usuario?.nombres.orEmpty()
                        apellidosEditados = usuario?.apellidos.orEmpty()
                        viewModel.resetearEstados()
                        mostrarModalEditar = true
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = usuario != null && !cargando,
                    colors = ButtonDefaults.buttonColors(containerColor = FinovaPrimaryContainer, contentColor = FinovaOnPrimary)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Editar datos", fontWeight = FontWeight.SemiBold)
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FinovaPrimary, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text("Finova protege tus datos personales.", style = FinovaBodySm, color = FinovaOnSurfaceVariant)
            }
        }
    }
}

@Composable
private fun DatoPersonal(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    protegido: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = FinovaLabelSm, color = FinovaOnSurfaceVariant, fontWeight = FontWeight.Medium)
        Surface(color = FinovaSurfaceContainerLow, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.padding(horizontal = 14.dp, vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = FinovaPrimary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Text(value.ifBlank { "No registrado" }, modifier = Modifier.weight(1f), style = FinovaBodyMd, color = FinovaOnSurface)
                if (protegido) Icon(Icons.Default.Lock, contentDescription = "No editable", tint = FinovaOnSurfaceVariant, modifier = Modifier.size(18.dp))
            }
        }
    }
}
