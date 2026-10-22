package com.example.Finova.feature.escaneo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Finova.core.design.theme.*

import android.net.Uri
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage

import androidx.compose.runtime.LaunchedEffect

import com.example.Finova.core.network.dto.CategoriaDto
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text

import com.example.Finova.core.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import com.example.Finova.core.util.SessionManager

import com.example.Finova.core.network.dto.ConfirmarComprobanteRequestDto
import com.example.Finova.core.network.dto.CuentaDto
import com.example.Finova.core.network.dto.MetodoPagoDto
import kotlinx.coroutines.launch

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale



// ============================================================
// FINOVA — Revisión de Comprobante con IA
// ============================================================

@Composable
fun ComprobanteRevisionScreen(
    onBack: () -> Unit,
    onConfirmar: () -> Unit,
    onDescartar: () -> Unit,
    imagenUri: Uri? = null
) {
    var monto by remember { mutableStateOf("") }
    var comercio by remember { mutableStateOf("") }
    var monedaActiva by remember { mutableStateOf("PEN") }
    var productosOCR by remember { mutableStateOf<List<String>>(emptyList()) }

    var textoOCR by remember { mutableStateOf("") }
    var procesandoOCR by remember { mutableStateOf(false) }
    var errorOCR by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    var menuCuentasAbierto by remember { mutableStateOf(false) }

    var cuentas by remember { mutableStateOf<List<CuentaDto>>(emptyList()) }
    var cuentaSeleccionada by remember { mutableStateOf<CuentaDto?>(null) }
    var metodosPago by remember { mutableStateOf<List<MetodoPagoDto>>(emptyList()) }
    var metodoPagoSeleccionado by remember { mutableStateOf<MetodoPagoDto?>(null) }
    var menuMetodosPagoAbierto by remember { mutableStateOf(false) }
    var guardando by remember { mutableStateOf(false) }
    var guardadoExitoso by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var errorMonto by remember { mutableStateOf<String?>(null) }
    var errorComercio by remember { mutableStateOf<String?>(null) }
    var errorCuenta by remember { mutableStateOf<String?>(null) }
    var errorMetodoPago by remember { mutableStateOf<String?>(null) }
    var errorCategoria by remember { mutableStateOf<String?>(null) }
    var tipoMovimiento by remember { mutableStateOf("GASTO") }

    // Solo mostrar cuentas cuya moneda coincide con la del comprobante.
    val cuentasMoneda = cuentas.filter {
        it.moneda.equals(monedaActiva, ignoreCase = true)
    }

    LaunchedEffect(monedaActiva, cuentas) {
        if (cuentaSeleccionada?.moneda?.equals(monedaActiva, ignoreCase = true) != true) {
            cuentaSeleccionada = cuentasMoneda.firstOrNull()
        }
        menuCuentasAbierto = false
        errorCuenta = null
    }

    var categorias by remember { mutableStateOf<List<CategoriaDto>>(emptyList()) }
    var categoriaSeleccionada by remember { mutableStateOf<CategoriaDto?>(null) }
    var menuCategoriasAbierto by remember { mutableStateOf(false) }
    var errorCategorias by remember { mutableStateOf(false) }

    val fechaHoraActual = remember {
        java.text.SimpleDateFormat(
            "'Hoy, 'dd MMM yyyy '·' HH:mm",
            java.util.Locale("es", "PE")
        ).format(java.util.Date())
    }

    // Cargar cuentas del usuario
    LaunchedEffect(Unit) {
        try {
            val session = SessionManager(context)
            val usuarioId = session.obtenerUsuarioId()

            if (usuarioId != -1L) {
                cuentas = ApiClient.apiService
                    .listarCuentasPorUsuario(usuarioId)
                metodosPago = ApiClient.apiService.listarMetodosPagoActivos()

                cuentaSeleccionada = cuentas.firstOrNull {
                    it.moneda.equals(monedaActiva, ignoreCase = true)
                }
                metodoPagoSeleccionado = metodosPago.firstOrNull()
            }
        } catch (e: Exception) {
            android.util.Log.e(
                "FinovaCuentas",
                "Error al cargar las cuentas",
                e
            )
        }
    }

// Cargar categorías de gasto
    LaunchedEffect(tipoMovimiento) {
        try {
            categorias = ApiClient.apiService
                .listarCategoriasPorTipo(tipoMovimiento)

            categoriaSeleccionada = null
            errorCategorias = false
        } catch (e: Exception) {
            categorias = emptyList()
            categoriaSeleccionada = null
            errorCategorias = true

            android.util.Log.e(
                "FinovaCategorias",
                "Error al cargar categorías",
                e
            )
        }
    }


    LaunchedEffect(imagenUri) {
        if (imagenUri == null) {
            textoOCR = ""
            monto = ""
            comercio = ""
            productosOCR = emptyList()
            return@LaunchedEffect
        }

        procesandoOCR = true
        errorOCR = false
        textoOCR = ""
        monto = ""
        comercio = ""
        productosOCR = emptyList()

        try {
            val bytes = withContext(Dispatchers.IO) {
                context.contentResolver
                    .openInputStream(imagenUri)
                    ?.use { it.readBytes() }
            } ?: throw Exception("No se pudo leer la imagen")

            val parteImagen = MultipartBody.Part.createFormData(
                "imagen",
                "comprobante.jpg",
                bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            )

            val resultado = ApiClient.apiService.analizarImagen(parteImagen)

            comercio = resultado.comercio.orEmpty()
            productosOCR = resultado.items.orEmpty().mapNotNull { item ->
                val nombre = item.nombre.orEmpty().trim()
                if (nombre.isBlank()) null else buildString {
                    append(nombre)
                    item.cantidad?.let { append(" × ").append(it) }
                    item.precio?.let { append(" — ").append(monedaActiva).append(" ").append(String.format(java.util.Locale.US, "%.2f", it)) }
                }
            }
            monto = resultado.total
                ?.takeIf { it > 0 }
                ?.let { String.format(java.util.Locale.US, "%.2f", it) }
                .orEmpty()

            monedaActiva = when (resultado.moneda?.uppercase()) {
                "USD", "$" -> "USD"
                else -> "PEN"
            }

            textoOCR = buildString {
                resultado.comercio?.let { appendLine("Comercio: $it") }
                resultado.ruc?.let { appendLine("RUC: $it") }
                resultado.fecha?.let { appendLine("Fecha: $it") }
                resultado.categoriaSugerida?.let {
                    appendLine("Categoría sugerida: $it")
                }
                resultado.total?.let { appendLine("Total: $it") }
                resultado.items?.forEach {
                    appendLine("${it.nombre.orEmpty()} x${it.cantidad ?: 1}: S/ ${it.precio ?: 0.0}")
                }
            }.ifBlank {
                resultado.razonamiento.orEmpty()
            }

        } catch (e: Exception) {
            errorOCR = true
            textoOCR = "Error: ${e.message ?: e.javaClass.simpleName}"
            android.util.Log.e("FinovaOCR", "Error al analizar comprobante", e)
        } finally {
            procesandoOCR = false
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { RevisionTopBar(onBack = onBack) },
        bottomBar = {
            RevisionBottomBar(
                onDescartar = onDescartar,
                onConfirmar = {
                    if (guardando) return@RevisionBottomBar

                    val montoNumerico = monto.toDoubleOrNull()

                    errorMonto = if (montoNumerico == null || montoNumerico <= 0) "Ingresa un monto válido." else null
                    errorComercio = if (comercio.isBlank()) "Completa el nombre del comercio o proveedor." else null
                    errorCuenta = if (cuentaSeleccionada == null) "Selecciona una cuenta." else null
                    errorCategoria = if (categoriaSeleccionada == null) "Selecciona una categoría." else null
                    errorMetodoPago = if (metodoPagoSeleccionado == null) "Selecciona un método de pago." else null

                    if (errorMonto == null && errorComercio == null && errorCuenta == null && errorCategoria == null && errorMetodoPago == null) {
                        scope.launch {
                            guardando = true
                            mensajeError = null

                            try {
                                val request = ConfirmarComprobanteRequestDto(
                                    tipoMovimiento = tipoMovimiento,
                                    cuentaOrigenId = if (tipoMovimiento == "GASTO") cuentaSeleccionada!!.id else null,
                                    cuentaDestinoId = if (tipoMovimiento == "INGRESO") cuentaSeleccionada!!.id else null,
                                    categoriaId = categoriaSeleccionada!!.id,
                                    metodoPagoId = metodoPagoSeleccionado!!.id,
                                    monto = montoNumerico!!,
                                    fechaMovimiento = java.text.SimpleDateFormat(
                                        "yyyy-MM-dd'T'HH:mm:ss",
                                        java.util.Locale.getDefault()
                                    ).format(java.util.Date()),
                                    descripcion =  null,
                                    nombreComercio = comercio.ifBlank { null },
                                    ruc = null,
                                    codigoOperacion = null,
                                    rutaComprobante = imagenUri?.toString()
                                )

                                withContext(Dispatchers.IO) {
                                    ApiClient.apiService.confirmarComprobante(request)
                                }

                                guardadoExitoso = true
                            } catch (e: Exception) {
                                mensajeError =
                                    "No se pudo guardar el comprobante: ${e.message ?: "Error desconocido"}"
                                android.util.Log.e(
                                    "FinovaGuardar",
                                    "Error al guardar comprobante",
                                    e
                                )
                            } finally {
                                guardando = false
                            }
                        }
                    }
                },
                guardando = guardando


            )

        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            // ---------- Chips de estado ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(FinovaTertiaryFixed)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = FinovaTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = when {
                            procesandoOCR -> "Leyendo comprobante..."
                            errorOCR -> "No se pudo leer el texto. Revisa la imagen."
                            textoOCR.isNotBlank() -> "Lectura OCR completada. Revisa los datos detectados."
                            else -> "Selecciona una imagen para comenzar."
                        },
                        style = FinovaLabelMd,
                        color = FinovaTertiary,
                        fontWeight = FontWeight.Medium
                    )
                }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(FinovaSurfaceContainerHigh)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = FinovaPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = if (procesandoOCR) "Procesando" else if (errorOCR) "Revisar imagen" else "OCR",
                        style = FinovaLabelSm,
                        color = FinovaOnSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ---------- Card de análisis IA ----------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(FinovaSurfaceContainerLowest)
                    .padding(14.dp)
            ) {
                // Miniatura del comprobante
                Box(
                    modifier = Modifier
                        .size(width = 70.dp, height = 90.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(FinovaSurfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    if (imagenUri != null) {
                        AsyncImage(
                            model = imagenUri,
                            contentDescription = "Boleta seleccionada",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Receipt,
                            contentDescription = null,
                            tint = FinovaOnSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = FinovaTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Análisis de comprobante",
                            style = FinovaTitleMd,
                            color = FinovaOnSurface,
                            fontWeight = FontWeight.SemiBold
                        )


                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = textoOCR.ifBlank { "Selecciona una imagen para intentar leer el comprobante. Verifica los datos antes de guardar." },
                        style = FinovaBodySm,
                        color = FinovaOnSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ---------- Monto detectado ----------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(FinovaSurfaceContainerLowest)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MONTO TOTAL DETECTADO",
                        style = FinovaLabelSm,
                        color = FinovaOnSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(FinovaTertiaryFixed)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = FinovaTertiary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = "Sugerido por IA",
                            style = FinovaLabelSm,
                            color = FinovaOnTertiaryFixedVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (monedaActiva == "PEN") "S/" else "$",
                            style = FinovaDisplayLgMobile,
                            color = FinovaPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(4.dp))
                        BasicTextField(
                            value = monto,
                            onValueChange = {
                                monto = it
                                if (it.toDoubleOrNull()?.let { valor -> valor > 0 } == true) errorMonto = null
                            },
                            modifier = Modifier.weight(1f),
                            textStyle = FinovaDisplayLgMobile.copy(
                                color = FinovaOnSurface,
                                fontWeight = FontWeight.Bold
                            ),
                            singleLine = true,
                            cursorBrush = SolidColor(FinovaPrimary)
                        )
                    }
                    // Selector PEN / USD
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(FinovaSurfaceContainerHigh)
                            .padding(4.dp)
                    ) {
                        SelectorMoneda(
                            texto = "PEN S/",
                            activo = monedaActiva == "PEN",
                            onClick = { monedaActiva = "PEN" }
                        )
                        SelectorMoneda(
                            texto = "USD $",
                            activo = monedaActiva == "USD",
                            onClick = { monedaActiva = "USD" }
                        )
                    }
                }
                if (errorMonto != null) {
                    Text(
                        text = errorMonto!!,
                        color = MaterialTheme.colorScheme.error,
                        style = FinovaBodySm,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ---------- Comercio ----------
            CampoRevision(
                label = "Comercio o Proveedor",
                etiquetaEstado = "Sugerido",
                iconoEstado = Icons.Default.AutoAwesome,
                iconoCampo = Icons.Outlined.Storefront,
                iconBg = FinovaSecondaryContainer,
                iconTint = FinovaOnSecondaryContainer,
                valor = comercio,
                onValorChange = {
                    comercio = it
                    if (it.isNotBlank()) errorComercio = null
                },
                trailingIcono = null,
                error = errorComercio
            )

            Spacer(Modifier.height(12.dp))

            // ---------- Fecha y hora ----------
            CampoRevision(
                label = "Fecha y Hora de Operación",
                etiquetaEstado = "OCR",
                iconoEstado = Icons.Default.Schedule,
                iconoCampo = Icons.Outlined.CalendarToday,
                iconBg = FinovaSurfaceContainerHigh,
                iconTint = FinovaOnSurfaceVariant,
                valor = fechaHoraActual,
                onValorChange = { },
                trailingIcono = Icons.Default.EditCalendar
            )

            Spacer(Modifier.height(12.dp))


            // ---------- Tipo de movimiento ----------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(FinovaSurfaceContainerLowest)
                    .padding(14.dp)
            ) {
                Text(
                    text = "Tipo de movimiento",
                    style = FinovaLabelSm,
                    color = FinovaOnSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(FinovaSurfaceContainerHigh)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SelectorMoneda(
                        texto = "Gasto",
                        activo = tipoMovimiento == "GASTO",
                        onClick = { tipoMovimiento = "GASTO" },
                        modifier = Modifier.weight(1f)
                    )
                    SelectorMoneda(
                        texto = "Ingreso",
                        activo = tipoMovimiento == "INGRESO",
                        onClick = { tipoMovimiento = "INGRESO" },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ---------- Categoría ----------
            Box(modifier = Modifier.fillMaxWidth()) {
                CampoRevisionCategoria(
                    categoria = categoriaSeleccionada?.nombre ?: "Elige categoría",
                    presupuestoRestante = if (errorCategorias) {
                        "No se pudieron cargar las categorías"
                    } else {
                        "Selecciona una categoría para ${if (tipoMovimiento == "GASTO") "este gasto" else "este ingreso"}"
                    },
                    icono = Icons.Outlined.Category,
                    onClick = { menuCategoriasAbierto = true }
                )

                DropdownMenu(
                    expanded = menuCategoriasAbierto,
                    onDismissRequest = { menuCategoriasAbierto = false }
                ) {
                    if (categorias.isEmpty()) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (errorCategorias) {
                                        "Error al cargar categorías"
                                    } else {
                                        "No hay categorías disponibles"
                                    }
                                )
                            },
                            onClick = { menuCategoriasAbierto = false }
                        )
                    } else {
                        categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria.nombre ?: "Sin nombre") },
                                onClick = {
                                    categoriaSeleccionada = categoria
                                    errorCategoria = null
                                    menuCategoriasAbierto = false
                                }
                            )
                        }
                    }
                }
            }
            if (errorCategoria != null) {
                Text(
                    text = errorCategoria!!,
                    color = MaterialTheme.colorScheme.error,
                    style = FinovaBodySm,
                    modifier = Modifier.padding(start = 14.dp, top = 4.dp)
                )
            }

            // ---------- Método de pago ----------
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(FinovaSurfaceContainerLowest)
                        .clickable { menuMetodosPagoAbierto = true }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(FinovaSurfaceContainerHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Payments,
                            contentDescription = null,
                            tint = FinovaPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = metodoPagoSeleccionado?.nombre ?: "Selecciona un método de pago",
                            style = FinovaTitleMd,
                            color = FinovaOnSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Método utilizado para esta operación",
                            style = FinovaBodySm,
                            color = FinovaOnSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Seleccionar método de pago",
                        tint = FinovaOnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                DropdownMenu(
                    expanded = menuMetodosPagoAbierto,
                    onDismissRequest = { menuMetodosPagoAbierto = false }
                ) {
                    if (metodosPago.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No hay métodos de pago disponibles") },
                            onClick = { menuMetodosPagoAbierto = false }
                        )
                    } else {
                        metodosPago.forEach { metodo ->
                            DropdownMenuItem(
                                text = { Text(metodo.nombre ?: "Método de pago ${metodo.id}") },
                                onClick = {
                                    metodoPagoSeleccionado = metodo
                                    errorMetodoPago = null
                                    menuMetodosPagoAbierto = false
                                }
                            )
                        }
                    }
                }
            }
            if (errorMetodoPago != null) {
                Text(
                    text = errorMetodoPago!!,
                    color = MaterialTheme.colorScheme.error,
                    style = FinovaBodySm,
                    modifier = Modifier.padding(start = 14.dp, top = 4.dp)
                )
            }
            Spacer(Modifier.height(12.dp))

            // ---------- Cuenta de pago ----------
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(FinovaSurfaceContainerLowest)
                        .clickable {
                            menuCuentasAbierto = true
                        }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(FinovaSurfaceContainerHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccountBalanceWallet,
                            contentDescription = null,
                            tint = FinovaPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = cuentaSeleccionada?.nombre
                                ?: "Selecciona una cuenta",
                            style = FinovaTitleMd,
                            color = FinovaOnSurface,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(Modifier.height(2.dp))

                        Text(
                            text = cuentaSeleccionada?.let {
                                "Saldo disponible: ${
                                    it.moneda ?: "PEN"
                                } ${String.format(
                                    java.util.Locale.US,
                                    "%.2f",
                                    it.saldoActual ?: 0.0
                                )}"
                            } ?: "No hay una cuenta seleccionada",
                            style = FinovaBodySm,
                            color = FinovaOnSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Seleccionar cuenta",
                        tint = FinovaOnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = menuCuentasAbierto,
                    onDismissRequest = { menuCuentasAbierto = false }
                ) {
                    cuentasMoneda.forEach { cuenta ->
                        DropdownMenuItem(
                            text = {
                                Text(cuenta.nombre ?: "Cuenta ${cuenta.id}")
                            },
                            onClick = {
                                cuentaSeleccionada = cuenta
                                errorCuenta = null
                                menuCuentasAbierto = false
                            }
                        )
                    }

                    if (cuentasMoneda.isEmpty()) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (monedaActiva == "PEN")
                                        "No tienes cuentas en soles"
                                    else
                                        "No tienes cuentas en dólares"
                                )
                            },
                            onClick = { menuCuentasAbierto = false }
                        )
                    }
                }
            }
            if (errorCuenta != null) {
                Text(
                    text = errorCuenta!!,
                    color = MaterialTheme.colorScheme.error,
                    style = FinovaBodySm,
                    modifier = Modifier.padding(start = 14.dp, top = 4.dp)
                )
            }

            // ---------- Productos detectados por OCR ----------
            if (productosOCR.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(FinovaSurfaceContainerLowest)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Productos detectados",
                        style = FinovaLabelLg,
                        color = FinovaOnSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    productosOCR.forEach { producto ->
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Outlined.ReceiptLong,
                                contentDescription = null,
                                tint = FinovaOnSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = producto,
                                style = FinovaBodyMd,
                                color = FinovaOnSurface
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    // Modal de error del OCR, con opción de volver a intentar desde la imagen.
    if (errorOCR) {
        AlertDialog(
            onDismissRequest = { errorOCR = false },
            title = { Text("No se pudo analizar la imagen") },
            text = {
                Text(
                    textoOCR.removePrefix("Error: ").ifBlank {
                        "Verifica la conexión e intenta nuevamente."
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        errorOCR = false
                        // Reinicia el análisis de la misma imagen.
                        textoOCR = ""
                        procesandoOCR = true
                        scope.launch {
                            try {
                                val uri = imagenUri ?: throw Exception("No hay imagen seleccionada")
                                val bytes = withContext(Dispatchers.IO) {
                                    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                                } ?: throw Exception("No se pudo leer la imagen")
                                val parteImagen = MultipartBody.Part.createFormData(
                                    "imagen",
                                    "comprobante.jpg",
                                    bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                                )
                                val resultado = ApiClient.apiService.analizarImagen(parteImagen)
                                comercio = resultado.comercio.orEmpty()
                                monto = resultado.total
                                    ?.takeIf { it > 0 }
                                    ?.let { String.format(java.util.Locale.US, "%.2f", it) }
                                    .orEmpty()
                                monedaActiva = when (resultado.moneda?.uppercase()) {
                                    "USD", "$" -> "USD"
                                    else -> "PEN"
                                }
                                productosOCR = resultado.items.orEmpty().mapNotNull { item ->
                                    val nombre = item.nombre.orEmpty().trim()
                                    if (nombre.isBlank()) null else buildString {
                                        append(nombre)
                                        item.cantidad?.let { append(" × ").append(it) }
                                        item.precio?.let {
                                            append(" — ").append(monedaActiva).append(" ")
                                                .append(String.format(java.util.Locale.US, "%.2f", it))
                                        }
                                    }
                                }
                                textoOCR = buildString {
                                    resultado.comercio?.let { appendLine("Comercio: $it") }
                                    resultado.ruc?.let { appendLine("RUC: $it") }
                                    resultado.fecha?.let { appendLine("Fecha: $it") }
                                    resultado.total?.let { appendLine("Total: $it") }
                                }
                                errorOCR = false
                            } catch (e: Exception) {
                                textoOCR = "Error: ${e.message ?: e.javaClass.simpleName}"
                                errorOCR = true
                            } finally {
                                procesandoOCR = false
                            }
                        }
                    }
                ) { Text("Reintentar") }
            },
            dismissButton = {
                TextButton(onClick = { errorOCR = false }) { Text("Cerrar") }
            }
        )
    }

    if (guardadoExitoso) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Comprobante guardado") },
            text = { Text("El movimiento se registró correctamente.") },
            confirmButton = {
                TextButton(onClick = onConfirmar) { Text("Continuar") }
            }
        )
    }

    // Modal para fallos al guardar.
    if (mensajeError != null) {
        AlertDialog(
            onDismissRequest = { mensajeError = null },
            title = { Text("No se pudo guardar") },
            text = { Text(mensajeError.orEmpty()) },
            confirmButton = {
                TextButton(onClick = { mensajeError = null }) { Text("Entendido") }
            }
        )
    }

    if (procesandoOCR || guardando) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(if (guardando) "Guardando comprobante" else "Analizando comprobante")
            },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(16.dp))
                    Text(
                        if (guardando) "Registrando el movimiento..."
                        else "Extrayendo los datos de la imagen..."
                    )
                }
            },
            confirmButton = { }
        )
    }
}

// ============================================================
// Componentes internos
// ============================================================

@Composable
private fun RevisionTopBar(onBack: () -> Unit) {
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
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Comprobante",
                style = FinovaTitleMd,
                color = FinovaOnSurface,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Más",
                    tint = FinovaOnSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun RevisionBottomBar(
    onDescartar: () -> Unit,
    onConfirmar: () -> Unit,
    guardando: Boolean = false
) {
    Surface(
        color = FinovaSurface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onDescartar,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FinovaSurfaceContainerHigh,
                    contentColor = FinovaOnSurface
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = FinovaOnSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Descartar",
                    style = FinovaLabelLg,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Button(
                onClick = onConfirmar,
                enabled = !guardando,
                modifier = Modifier
                    .weight(1.5f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FinovaPrimary,
                    contentColor = FinovaOnPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Confirmar y Guardar",
                    style = FinovaLabelLg,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SelectorMoneda(
    texto: String,
    activo: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (activo) FinovaPrimary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            style = FinovaLabelSm,
            color = if (activo) FinovaOnPrimary else FinovaOnSurfaceVariant,
            fontWeight = if (activo) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}



@Composable
private fun CampoRevision(
    label: String,
    etiquetaEstado: String,
    iconoEstado: androidx.compose.ui.graphics.vector.ImageVector,
    iconoCampo: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    valor: String,
    onValorChange: (String) -> Unit,
    trailingIcono: androidx.compose.ui.graphics.vector.ImageVector?,
    error: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FinovaSurfaceContainerLowest)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = FinovaLabelSm,
                color = FinovaOnSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = iconoEstado,
                    contentDescription = null,
                    tint = FinovaTertiary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = etiquetaEstado,
                    style = FinovaLabelSm,
                    color = FinovaTertiary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconoCampo,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            BasicTextField(
                value = valor,
                onValueChange = onValorChange,
                modifier = Modifier.weight(1f),
                textStyle = FinovaTitleMd.copy(color = FinovaOnSurface),
                singleLine = true,
                cursorBrush = SolidColor(FinovaPrimary)
            )
            if (trailingIcono != null) {
                Icon(
                    imageVector = trailingIcono,
                    contentDescription = null,
                    tint = FinovaOnSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = FinovaBodySm,
                modifier = Modifier.padding(start = 52.dp, top = 6.dp)
            )
        }
    }
}

@Composable
private fun CampoRevisionCategoria(
    categoria: String,
    presupuestoRestante: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FinovaSurfaceContainerLowest)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(FinovaSecondary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = FinovaOnSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = categoria,
                    style = FinovaTitleMd,
                    color = FinovaOnSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(FinovaSecondaryContainer)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoFixHigh,
                            contentDescription = null,
                            tint = FinovaOnSecondaryContainer,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = "Clasificado",
                            style = FinovaLabelSm,
                            color = FinovaOnSecondaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = presupuestoRestante,
                style = FinovaBodySm,
                color = FinovaOnSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.ExpandMore,
            contentDescription = null,
            tint = FinovaOnSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

