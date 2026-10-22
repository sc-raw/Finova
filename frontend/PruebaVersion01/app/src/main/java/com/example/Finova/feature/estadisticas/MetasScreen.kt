package com.example.Finova.feature.estadisticas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.Finova.core.design.theme.*
import androidx.compose.ui.platform.LocalContext
import com.example.Finova.core.network.ApiClient
import com.example.Finova.core.network.MetaRequestDto
import com.example.Finova.core.network.MetaUsuarioRequestDto
import com.example.Finova.core.network.dto.MetaDto
import com.example.Finova.core.network.dto.CuentaDto
import com.example.Finova.core.util.SessionManager
import kotlinx.coroutines.launch
import com.example.Finova.feature.movimientos.NuevaTransaccionSheet

// ============================================================
// FINOVA — Metas de Ahorro
// Vertical 5 · feature/estadisticas
// ============================================================

private data class MetaVisual(
    val id: Long,
    val titulo: String,
    val descripcion: String,
    val actual: Double,
    val objetivo: Double,
    val porcentaje: Int,
    val estadoTexto: String,
    val estadoColor: Color,
    val footerLabel: String,
    val footerValor: String,
    val icono: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val progressColor: Color
)

@Composable
fun MetasScreen(
    onNavigate: (String) -> Unit,
    onAnadirFondos: (Long) -> Unit,
    onBack: () -> Unit,
    onImagenSeleccionada: (Uri) -> Unit
) {
    var mostrarSheet by remember { mutableStateOf(false) }
    var mostrarFormularioMeta by remember { mutableStateOf(false) }
    var mostrarEliminarMeta by remember { mutableStateOf(false) }
    var metaRetirando by remember { mutableStateOf<MetaDto?>(null) }
    var metaEditando by remember { mutableStateOf<MetaDto?>(null) }

    val selectorImagen = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) onImagenSeleccionada(uri)
    }

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val usuarioId = remember { sessionManager.obtenerUsuarioId() }
    val scope = rememberCoroutineScope()
    var metasDto by remember { mutableStateOf<List<MetaDto>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    fun cargarMetas() {
        scope.launch {
            cargando = true
            error = null
            try {
                if (usuarioId == -1L) {
                    error = "No hay una sesión activa."
                } else {
                    metasDto = ApiClient.apiService.listarMetasPorUsuario(usuarioId)
                }
            } catch (e: Exception) {
                error = e.message ?: "No se pudieron cargar las metas."
            } finally {
                cargando = false
            }
        }
    }

    LaunchedEffect(usuarioId) { cargarMetas() }

    val metas = metasDto.mapIndexed { index, meta ->
        val actual = meta.montoActual ?: 0.0
        val objetivo = meta.montoObjetivo ?: 0.0
        val porcentaje = if (objetivo > 0.0) ((actual / objetivo) * 100).toInt().coerceIn(0, 100) else 0
        val paleta = when (index % 3) {
            0 -> Triple(FinovaSecondaryContainer, FinovaSecondary, FinovaPrimary)
            1 -> Triple(FinovaTertiaryFixed, FinovaTertiary, FinovaTertiary)
            else -> Triple(FinovaSecondaryFixed, FinovaSecondary, FinovaSecondary)
        }
        // Todas las metas utilizan un icono general de ahorro.
        val icono = Icons.Outlined.Savings
        MetaVisual(
            id = meta.id,
            titulo = meta.nombre ?: "Meta sin nombre",
            descripcion = meta.fechaObjetivo?.let { "Fecha objetivo: $it" } ?: "Meta de ahorro",
            actual = actual,
            objetivo = objetivo,
            porcentaje = porcentaje,
            estadoTexto = when {
                porcentaje >= 100 -> "Completada"
                porcentaje >= 75 -> "Meta casi lista"
                porcentaje >= 40 -> "En curso"
                else -> "Iniciando"
            },
            estadoColor = paleta.second,
            footerLabel = "Te faltan",
            footerValor = "${if ((meta.moneda ?: "PEN").uppercase() == "USD") "US$" else "S/"} %.2f".format((objetivo - actual).coerceAtLeast(0.0)),
            icono = icono,
            iconBg = paleta.first,
            iconTint = paleta.second,
            progressColor = paleta.third
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { MetasTopBar(onBack = onBack) },
        bottomBar = {
            com.example.Finova.core.design.components.BottomNavBar(
                currentRoute = "metas",
                onNavigate = onNavigate,
                onScanClick = { mostrarSheet = true }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // ---------- Encabezado ----------
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Metas de Ahorro",
                            style = FinovaHeadlineMd,
                            color = FinovaOnSurface
                        )
                        Spacer(Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(FinovaPrimary)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "${metas.size} metas activas",
                                style = FinovaBodySm,
                                color = FinovaOnSurfaceVariant
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(FinovaSurfaceContainerHigh)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.TrendingUp,
                            contentDescription = null,
                            tint = FinovaPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Ahorro total",
                            style = FinovaLabelSm,
                            color = FinovaPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // ---------- Subtítulo "Tus Planes Activos" ----------
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tus Planes Activos",
                        style = FinovaLabelLg,
                        color = FinovaOnSurface
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Prioridad",
                            style = FinovaLabelSm,
                            color = FinovaPrimary
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Outlined.SwapVert,
                            contentDescription = null,
                            tint = FinovaPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // ---------- Estado de carga / error / metas ----------
            if (cargando) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = FinovaPrimary)
                    }
                }
            } else if (error != null) {
                item {
                    Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(error ?: "Error al cargar las metas", color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(onClick = { cargarMetas() }) { Text("Reintentar") }
                    }
                }
            } else if (metas.isEmpty()) {
                item {
                    Text(
                        text = "Aún no tienes metas de ahorro.",
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        color = FinovaOnSurfaceVariant
                    )
                }
            } else {
                items(metas.size) { index ->
                    MetaCard(
                        meta = metas[index],
                        onAnadirFondos = { onAnadirFondos(metas[index].id) },
                        onRetirarAhorros = { metaRetirando = metasDto.firstOrNull { it.id == metas[index].id } },
                        onEditar = {
                            metaEditando = metasDto.firstOrNull { it.id == metas[index].id }
                            mostrarFormularioMeta = true
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }

            // ---------- Botón crear meta ----------
            item {
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = {
                        metaEditando = null
                        mostrarFormularioMeta = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FinovaPrimary,
                        contentColor = FinovaOnPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Crear nueva meta",
                        style = FinovaLabelLg,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { mostrarEliminarMeta = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Eliminar meta", style = FinovaLabelLg, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (mostrarEliminarMeta) {
        EliminarMetaDialog(
            metas = metasDto,
            usuarioId = usuarioId,
            onDismiss = { mostrarEliminarMeta = false },
            onEliminada = {
                mostrarEliminarMeta = false
                cargarMetas()
            }
        )
    }

    if (metaRetirando != null) {
        RetirarAhorrosDialog(
            meta = metaRetirando!!,
            usuarioId = usuarioId,
            onDismiss = { metaRetirando = null },
            onRetirado = {
                metaRetirando = null
                cargarMetas()
            }
        )
    }

    if (mostrarFormularioMeta) {
        MetaFormDialog(
            meta = metaEditando,
            usuarioId = usuarioId,
            onDismiss = { mostrarFormularioMeta = false },
            onGuardado = {
                mostrarFormularioMeta = false
                metaEditando = null
                cargarMetas()
            }
        )
    }

    // ---------- Bottom Sheet ----------
    if (mostrarSheet) {
        NuevaTransaccionSheet(
            onDismiss = { mostrarSheet = false },
            onEscanear = {
                mostrarSheet = false
                onNavigate("escanear")
            },
            onSubirImagen = {
                mostrarSheet = false
                selectorImagen.launch("image/*")
            },
            onAgregarManual = {
                mostrarSheet = false
                onNavigate("nuevo_movimiento")
            },
            onTransferir = {
                mostrarSheet = false
                onNavigate("transferir")
            }
        )
    }
}

// ============================================================
// Componentes internos
// ============================================================

@Composable
private fun MetasTopBar(onBack: () -> Unit) {
    Surface(
        color = FinovaSurface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = FinovaOnSurface
                )
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(FinovaPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "F",
                    style = FinovaLabelLg,
                    color = FinovaOnPrimary
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "FINOVA",
                    style = FinovaLabelSm,
                    color = FinovaPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Metas",
                    style = FinovaHeadlineSm,
                    color = FinovaOnSurface
                )
            }
        }
    }
}

@Composable
private fun MetaCard(
    meta: MetaVisual,
    onAnadirFondos: () -> Unit,
    onRetirarAhorros: () -> Unit,
    onEditar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(FinovaSurfaceContainerLowest)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(meta.iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = meta.icono,
                    contentDescription = null,
                    tint = meta.iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meta.titulo,
                    style = FinovaHeadlineSm,
                    color = FinovaOnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = meta.descripcion,
                    style = FinovaBodySm,
                    color = FinovaOnSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onEditar, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar meta",
                    tint = FinovaPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(meta.iconBg)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = meta.iconTint,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${meta.porcentaje}%",
                        style = FinovaLabelSm,
                        color = meta.iconTint,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "S/ %.2f".format(meta.actual),
                    style = FinovaHeadlineLg,
                    color = FinovaOnSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "/ S/ %.2f".format(meta.objetivo),
                    style = FinovaBodySm,
                    color = FinovaOnSurfaceVariant
                )
            }
            Text(
                text = meta.estadoTexto,
                style = FinovaLabelSm,
                color = meta.estadoColor,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(FinovaSurfaceContainer)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(meta.porcentaje / 100f)
                    .clip(RoundedCornerShape(999.dp))
                    .background(meta.progressColor)
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = "${meta.footerLabel}: ${meta.footerValor}",
            style = FinovaLabelSm,
            color = FinovaOnSurfaceVariant,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onAnadirFondos,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FinovaPrimary, contentColor = FinovaOnPrimary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(5.dp))
                Text("Añadir fondos", style = FinovaLabelSm, maxLines = 1)
            }
            OutlinedButton(
                onClick = onRetirarAhorros,
                enabled = meta.actual > 0.0,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = FinovaPrimary)
            ) {
                Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(5.dp))
                Text("Retirar ahorros", style = FinovaLabelSm, maxLines = 1)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RetirarAhorrosDialog(
    meta: MetaDto,
    usuarioId: Long,
    onDismiss: () -> Unit,
    onRetirado: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var cuentas by remember { mutableStateOf<List<CuentaDto>>(emptyList()) }
    var cuentaSeleccionada by remember { mutableStateOf<CuentaDto?>(null) }
    var monto by remember { mutableStateOf("") }
    var desplegarCuentas by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(true) }
    var enviando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(meta.id, usuarioId) {
        try {
            if (usuarioId == -1L) error = "No hay una sesión activa."
            else {
                val monedaMeta = (meta.moneda ?: "PEN").uppercase()
                cuentas = ApiClient.apiService.listarCuentasPorUsuario(usuarioId)
                    .filter { (it.moneda ?: "PEN").uppercase() == monedaMeta }
                cuentaSeleccionada = cuentas.firstOrNull()
                if (cuentas.isEmpty()) error = "No tienes cuentas en ${if (monedaMeta == "USD") "dólares" else "soles"} para recibir el dinero."
            }
        } catch (e: Exception) {
            error = e.message ?: "No se pudieron cargar las cuentas."
        } finally { cargando = false }
    }

    val montoNum = monto.replace(',', '.').toDoubleOrNull() ?: 0.0
    val saldoMeta = meta.montoActual ?: 0.0
    val simboloMeta = if ((meta.moneda ?: "PEN").uppercase() == "USD") "US$" else "S/"

    AlertDialog(
        onDismissRequest = { if (!enviando) onDismiss() },
        title = { Text("Retirar ahorros", color = FinovaOnSurface) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Meta: ${meta.nombre.orEmpty()}", color = FinovaOnSurfaceVariant, style = FinovaBodySm)
                Text("Disponible: $simboloMeta %.2f".format(saldoMeta), color = FinovaOnSurface, fontWeight = FontWeight.SemiBold)
                ExposedDropdownMenuBox(
                    expanded = desplegarCuentas,
                    onExpandedChange = { desplegarCuentas = !desplegarCuentas }
                ) {
                    OutlinedTextField(
                        value = cuentaSeleccionada?.let { "${it.nombre ?: it.bancoNombre ?: "Cuenta"} · ${it.moneda ?: "PEN"}" }.orEmpty(),
                        onValueChange = {}, readOnly = true, label = { Text("Cuenta de destino") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = desplegarCuentas) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = desplegarCuentas, onDismissRequest = { desplegarCuentas = false }) {
                        cuentas.forEach { cuenta ->
                            DropdownMenuItem(
                                text = { Text("${cuenta.nombre ?: cuenta.bancoNombre ?: "Cuenta"} · ${cuenta.moneda ?: "PEN"}") },
                                onClick = { cuentaSeleccionada = cuenta; desplegarCuentas = false }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = monto,
                    onValueChange = { nuevo -> if (nuevo.isEmpty() || nuevo.matches(Regex("^\\d*[.,]?\\d{0,2}$"))) monto = nuevo },
                    label = { Text("Monto a retirar ($simboloMeta)") }, singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error, style = FinovaBodySm)
            }
        },
        confirmButton = {
            Button(
                enabled = !cargando && !enviando && cuentaSeleccionada != null && montoNum > 0 && montoNum <= saldoMeta,
                onClick = {
                    scope.launch {
                        enviando = true; error = null
                        try {
                            ApiClient.apiService.retirarDeMeta(meta.id, mapOf("cuentaId" to cuentaSeleccionada!!.id.toDouble(), "monto" to montoNum))
                            onRetirado()
                        } catch (e: Exception) {
                            error = e.message ?: "No se pudo retirar el dinero."
                        } finally { enviando = false }
                    }
                }
            ) { if (enviando) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp) else Text("Confirmar retiro") }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !enviando) { Text("Cancelar") } },
        containerColor = FinovaSurface
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MetaFormDialog(
    meta: MetaDto?,
    usuarioId: Long,
    onDismiss: () -> Unit,
    onGuardado: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var nombre by remember(meta?.id) { mutableStateOf(meta?.nombre.orEmpty()) }
    var moneda by remember(meta?.id) { mutableStateOf((meta?.moneda ?: "PEN").uppercase()) }
    var objetivo by remember(meta?.id) { mutableStateOf(meta?.montoObjetivo?.let { if (it > 0) it.toString() else "" }.orEmpty()) }
    var guardando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var mostrarErrorMonto by remember { mutableStateOf(false) }

    val montoObjetivo = objetivo.replace(',', '.').toDoubleOrNull() ?: 0.0
    val montoActual = meta?.montoActual ?: 0.0

    AlertDialog(
        onDismissRequest = { if (!guardando) onDismiss() },
        title = {
            Text(if (meta == null) "Crear nueva meta" else "Modificar meta", color = FinovaOnSurface)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it; error = null },
                    label = { Text("Nombre de la meta") },
                    placeholder = { Text("Ej. Laptop nueva") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (meta == null) {
                    Text("Moneda de la meta", style = FinovaLabelSm, color = FinovaOnSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = moneda == "PEN", onClick = { moneda = "PEN" }, label = { Text("Soles (PEN)") })
                        FilterChip(selected = moneda == "USD", onClick = { moneda = "USD" }, label = { Text("Dólares (USD)") })
                    }
                } else {
                    Text("Moneda: ${if (moneda == "USD") "Dólares (USD)" else "Soles (PEN)"} · No se puede cambiar después de crear la meta", style = FinovaBodySm, color = FinovaOnSurfaceVariant)
                }
                OutlinedTextField(
                    value = objetivo,
                    onValueChange = { nuevo ->
                        if (nuevo.isEmpty() || nuevo.matches(Regex("^\\d*[.,]?\\d{0,2}$"))) {
                            objetivo = nuevo
                            error = null
                        }
                    },
                    label = { Text("Monto objetivo (${if (moneda == "USD") "US$" else "S/"})") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                if (meta != null) {
                    Text("Dinero aportado: ${if (moneda == "USD") "US$" else "S/"} %.2f".format(montoActual), style = FinovaBodySm, color = FinovaOnSurfaceVariant)
                }
                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, style = FinovaBodySm)
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !guardando,
                onClick = {
                    when {
                        nombre.isBlank() -> error = "Ingresa el nombre de la meta."
                        montoObjetivo <= 0.0 -> error = "El monto objetivo debe ser mayor que cero."
                        meta != null && montoObjetivo < montoActual -> mostrarErrorMonto = true
                        usuarioId == -1L -> error = "No hay una sesión activa."
                        else -> {
                            scope.launch {
                                guardando = true
                                error = null
                                try {
                                    val cuerpo = MetaRequestDto(
                                        usuario = MetaUsuarioRequestDto(meta?.usuarioId ?: usuarioId),
                                        nombre = nombre.trim(),
                                        montoObjetivo = montoObjetivo,
                                        montoActual = meta?.montoActual ?: 0.0,
                                        moneda = meta?.moneda ?: moneda
                                    )
                                    if (meta == null) ApiClient.apiService.guardarMeta(cuerpo)
                                    else ApiClient.apiService.actualizarMeta(meta.id, cuerpo)
                                    onGuardado()
                                } catch (e: Exception) {
                                    error = e.message ?: "No se pudo guardar la meta."
                                } finally {
                                    guardando = false
                                }
                            }
                        }
                    }
                }
            ) {
                if (guardando) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                else Text(if (meta == null) "Crear meta" else "Guardar cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !guardando) { Text("Cancelar") }
        },
        containerColor = FinovaSurface
    )

    if (mostrarErrorMonto) {
        AlertDialog(
            onDismissRequest = { mostrarErrorMonto = false },
            icon = { Icon(Icons.Outlined.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Monto objetivo inválido") },
            text = {
                Text(
                    "El monto objetivo no puede ser menor que el dinero aportado a la meta. Dinero aportado: S/ %.2f. Ingresa un monto igual o superior a S/ %.2f.".format(montoActual, montoActual)
                )
            },
            confirmButton = { Button(onClick = { mostrarErrorMonto = false }) { Text("Entendido") } },
            containerColor = FinovaSurface
        )
    }
}
