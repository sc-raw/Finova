package com.example.Finova.core.network

import com.example.Finova.core.network.dto.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {

    // ==========================================================
    // ==================== USUARIOS ============================
    // ==========================================================

    @GET("api/usuarios")
    suspend fun listarUsuarios(): List<UsuarioDto>

    @GET("api/usuarios/{id}")
    suspend fun obtenerUsuario(@Path("id") id: Long): UsuarioDto

    @POST("api/usuarios")
    suspend fun registrar(@Body request: RegistroRequestDto): UsuarioDto

    @PUT("api/usuarios/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: Long,
        @Body usuario: UsuarioDto
    ): UsuarioDto

    @DELETE("api/usuarios/{id}")
    suspend fun eliminarUsuario(@Path("id") id: Long)

    @POST("api/usuarios/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @PUT("api/usuarios/{id}/password")
    suspend fun cambiarPassword(
        @Path("id") id: Long,
        @Body request: CambiarPasswordRequestDto
    )

    // ==========================================================
    // ==================== BANCOS ==============================
    // ==========================================================

    @GET("api/bancos")
    suspend fun listarBancos(): List<BancoDto>

    @GET("api/bancos/activos")
    suspend fun listarBancosActivos(): List<BancoDto>

    @GET("api/bancos/{id}")
    suspend fun obtenerBanco(@Path("id") id: Long): BancoDto

    @POST("api/bancos")
    suspend fun guardarBanco(@Body banco: BancoDto): BancoDto

    @PUT("api/bancos/{id}")
    suspend fun actualizarBanco(
        @Path("id") id: Long,
        @Body banco: BancoDto
    ): BancoDto

    @DELETE("api/bancos/{id}")
    suspend fun eliminarBanco(@Path("id") id: Long)

    // ==========================================================
    // ==================== CATEGORÍAS ==========================
    // ==========================================================

    @GET("api/categorias")
    suspend fun listarCategorias(): List<CategoriaDto>

    @GET("api/categorias/tipo/{tipo}")
    suspend fun listarCategoriasPorTipo(
        @Path("tipo") tipo: String
    ): List<CategoriaDto>

    @GET("api/categorias/{id}")
    suspend fun obtenerCategoria(@Path("id") id: Long): CategoriaDto

    @POST("api/categorias")
    suspend fun guardarCategoria(@Body categoria: CategoriaDto): CategoriaDto

    @PUT("api/categorias/{id}")
    suspend fun actualizarCategoria(
        @Path("id") id: Long,
        @Body categoria: CategoriaDto
    ): CategoriaDto

    @DELETE("api/categorias/{id}")
    suspend fun eliminarCategoria(@Path("id") id: Long)

    // ==========================================================
    // ==================== MÉTODOS DE PAGO =====================
    // ==========================================================

    @GET("api/metodos-pago")
    suspend fun listarMetodosPago(): List<MetodoPagoDto>

    @GET("api/metodos-pago/activos")
    suspend fun listarMetodosPagoActivos(): List<MetodoPagoDto>

    @GET("api/metodos-pago/{id}")
    suspend fun obtenerMetodoPago(@Path("id") id: Long): MetodoPagoDto

    @POST("api/metodos-pago")
    suspend fun guardarMetodoPago(
        @Body metodoPago: MetodoPagoDto
    ): MetodoPagoDto

    @PUT("api/metodos-pago/{id}")
    suspend fun actualizarMetodoPago(
        @Path("id") id: Long,
        @Body metodoPago: MetodoPagoDto
    ): MetodoPagoDto

    @DELETE("api/metodos-pago/{id}")
    suspend fun eliminarMetodoPago(@Path("id") id: Long)

    // ==========================================================
    // ==================== CUENTAS =============================
    // ==========================================================

    @GET("api/cuentas")
    suspend fun listarCuentas(): List<CuentaDto>

    @GET("api/cuentas/usuario/{usuarioId}")
    suspend fun listarCuentasPorUsuario(
        @Path("usuarioId") usuarioId: Long
    ): List<CuentaDto>

    @GET("api/cuentas/{id}")
    suspend fun obtenerCuenta(@Path("id") id: Long): CuentaDto

    @POST("api/cuentas")
    suspend fun guardarCuenta(@Body cuenta: CuentaDto): CuentaDto

    @PUT("api/cuentas/{id}")
    suspend fun actualizarCuenta(
        @Path("id") id: Long,
        @Body cuenta: CuentaDto
    ): CuentaDto

    @DELETE("api/cuentas/{id}")
    suspend fun eliminarCuenta(@Path("id") id: Long)

    @PUT("api/cuentas/{id}/sumar")
    suspend fun sumarSaldo(
        @Path("id") id: Long,
        @Body body: Map<String, Double>
    )

    @PUT("api/cuentas/{id}/restar")
    suspend fun restarSaldo(
        @Path("id") id: Long,
        @Body body: Map<String, Double>
    )

    // ==========================================================
    // ==================== MOVIMIENTOS =========================
    // ==========================================================

    @GET("api/movimientos")
    suspend fun listarMovimientos(): List<MovimientoDto>

    @GET("api/movimientos/usuario/{usuarioId}")
    suspend fun listarMovimientosPorUsuario(
        @Path("usuarioId") usuarioId: Long
    ): List<MovimientoDto>

    @GET("api/movimientos/cuenta/{cuentaId}")
    suspend fun listarMovimientosPorCuenta(
        @Path("cuentaId") cuentaId: Long
    ): List<MovimientoDto>

    @GET("api/movimientos/{id}")
    suspend fun obtenerMovimiento(@Path("id") id: Long): MovimientoDto

    @POST("api/movimientos")
    suspend fun guardarMovimiento(
        @Body movimiento: MovimientoDto
    ): MovimientoDto

    @PUT("api/movimientos/{id}")
    suspend fun actualizarMovimiento(
        @Path("id") id: Long,
        @Body movimiento: MovimientoDto
    ): MovimientoDto

    @DELETE("api/movimientos/{id}")
    suspend fun eliminarMovimiento(@Path("id") id: Long)

    @POST("api/movimientos/gasto")
    suspend fun registrarGasto(@Body request: RegistrarGastoRequestDto)

    @POST("api/movimientos/ingreso")
    suspend fun registrarIngreso(@Body request: RegistrarIngresoRequestDto)

    @POST("api/movimientos/transferir")
    suspend fun transferir(@Body request: TransferirRequestDto)

    @POST("api/movimientos/aportar-meta")
    suspend fun aportarMeta(@Body request: AportarMetaRequestDto)



    @GET("api/movimientos/usuario/{usuarioId}/tipo/{tipo}")
    suspend fun listarMovimientosPorTipoYUsuario(
        @Path("tipo") tipo: String,
        @Path("usuarioId") usuarioId: Long
    ): List<MovimientoDto>



    // ==========================================================
    // ==================== METAS ===============================
    // ==========================================================

    @GET("api/metas")
    suspend fun listarMetas(): List<MetaDto>

    @GET("api/metas/usuario/{usuarioId}")
    suspend fun listarMetasPorUsuario(
        @Path("usuarioId") usuarioId: Long
    ): List<MetaDto>

    @GET("api/metas/{id}")
    suspend fun obtenerMeta(@Path("id") id: Long): MetaDto

    @GET("api/metas/{id}/progreso")
    suspend fun calcularProgresoMeta(@Path("id") id: Long): Double

    @POST("api/metas")
    suspend fun guardarMeta(@Body meta: MetaDto): MetaDto

    @PUT("api/metas/{id}")
    suspend fun actualizarMeta(
        @Path("id") id: Long,
        @Body meta: MetaDto
    ): MetaDto

    @DELETE("api/metas/{id}")
    suspend fun eliminarMeta(@Path("id") id: Long)

    @PUT("api/metas/{id}/aportar")
    suspend fun aportarAMeta(
        @Path("id") id: Long,
        @Body body: Map<String, Double>
    )

    @PUT("api/metas/{id}/retirar")
    suspend fun retirarDeMeta(
        @Path("id") id: Long,
        @Body body: Map<String, Double>
    )

    // ==========================================================
    // ==================== ESTADÍSTICAS ========================
    // ==========================================================

    @GET("api/estadisticas/{usuarioId}")
    suspend fun obtenerEstadisticas(
        @Path("usuarioId") usuarioId: Long,
        @Query("periodo") periodo: String = "mes"
    ): EstadisticasResponseDto

    // ==========================================================
    // ==================== COMPROBANTES ========================
    // ==========================================================

    @GET("api/comprobantes")
    suspend fun listarComprobantes(): List<ComprobanteDto>

    @GET("api/comprobantes/{id}")
    suspend fun obtenerComprobante(@Path("id") id: Long): ComprobanteDto

    @GET("api/comprobantes/movimiento/{movimientoId}")
    suspend fun obtenerComprobantePorMovimiento(
        @Path("movimientoId") movimientoId: Long
    ): ComprobanteDto

    @POST("api/comprobantes")
    suspend fun guardarComprobante(
        @Body comprobante: ComprobanteDto
    ): ComprobanteDto

    @PUT("api/comprobantes/{id}")
    suspend fun actualizarComprobante(
        @Path("id") id: Long,
        @Body comprobante: ComprobanteDto
    ): ComprobanteDto

    @DELETE("api/comprobantes/{id}")
    suspend fun eliminarComprobante(@Path("id") id: Long)

    @POST("api/comprobantes/interpretar")
    suspend fun interpretarComprobante(
        @Body request: InterpretarComprobanteRequestDto
    ): InterpretarComprobanteResponseDto

    @POST("api/movimientos/confirmar-comprobante")
    suspend fun confirmarComprobante(
        @Body request: ConfirmarComprobanteRequestDto
    ): MovimientoDto


    @Multipart
    @POST("api/comprobantes/analizar-imagen")
    suspend fun analizarImagen(
        @Part imagen: MultipartBody.Part
    ): InterpretarComprobanteResponseDto

    // ==========================================================
    // ==================== TEST ================================
    // ==========================================================

    @GET("api/test")
    suspend fun testConexion(): String
}