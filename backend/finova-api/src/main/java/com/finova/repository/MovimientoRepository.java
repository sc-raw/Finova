package com.finova.repository;

import com.finova.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.finova.entity.TipoMovimiento;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    // Listar movimientos de un usuario (a través de la cuenta origen)
    List<Movimiento> findByCuentaOrigenUsuarioIdOrderByFechaMovimientoDesc(Long usuarioId);

    // Movimientos de una cuenta específica
    List<Movimiento> findByCuentaOrigenIdOrderByFechaMovimientoDesc(Long cuentaId);

    // Movimientos por tipo (GASTO, INGRESO, etc.)
    List<Movimiento> findByTipoMovimientoAndCuentaOrigenUsuarioId(TipoMovimiento tipo, Long usuarioId);

    // ==================== SP ====================

    // SP: Dashboard resumen (devuelve 3 result sets: saldos, totales del mes, últimos 4)
    @Procedure(name = "sp_dashboard_resumen")
    List<Object[]> dashboardResumen(@Param("p_user_id") Integer userId);

    // SP: Historial de movimientos con filtros
    @Procedure(name = "sp_historial_movimientos")
    List<Object[]> historialMovimientos(
        @Param("p_user_id") Integer userId,
        @Param("p_tipo") String tipo,
        @Param("p_busqueda") String busqueda,
        @Param("p_cuenta_id") Integer cuentaId
    );

    // SP: Detalle de movimiento
    @Procedure(name = "sp_detalle_movimiento")
    List<Object[]> detalleMovimiento(@Param("p_transaction_id") Integer transactionId);

    // SP: Guardar transacción escaneada
    @Procedure(name = "sp_guardar_transaccion_escaneada")
    void guardarTransaccionEscaneada(
        @Param("p_user_id") Integer userId,
        @Param("p_account_id") Integer accountId,
        @Param("p_category_id") Integer categoryId,
        @Param("p_amount") BigDecimal amount,
        @Param("p_currency") String currency,
        @Param("p_exchange_rate") BigDecimal exchangeRate,
        @Param("p_occurred_at") LocalDateTime occurredAt,
        @Param("p_note") String note,
        @Param("p_type") String type,
        @Param("p_image_url") String imageUrl,
        @Param("p_ocr_text") String ocrText,
        @Param("p_gemini_response") String geminiResponse,
        @Param("p_confidence") BigDecimal confidence,
        @Param("p_items") String itemsJson
    );

    // SP: Guardar transacción manual
    @Procedure(name = "sp_guardar_transaccion_manual")
    void guardarTransaccionManual(
        @Param("p_user_id") Integer userId,
        @Param("p_account_id") Integer accountId,
        @Param("p_category_id") Integer categoryId,
        @Param("p_amount") BigDecimal amount,
        @Param("p_currency") String currency,
        @Param("p_exchange_rate") BigDecimal exchangeRate,
        @Param("p_occurred_at") LocalDateTime occurredAt,
        @Param("p_note") String note,
        @Param("p_type") String type
    );

    // SP: Transferir entre cuentas
    @Procedure(name = "sp_transferir")
    void transferir(
        @Param("p_user_id") Integer userId,
        @Param("p_origen_id") Integer origenId,
        @Param("p_destino_id") Integer destinoId,
        @Param("p_monto") BigDecimal monto,
        @Param("p_linked_amount") BigDecimal linkedAmount,
        @Param("p_exchange_rate") BigDecimal exchangeRate,
        @Param("p_descripcion") String descripcion
    );

    // SP: Eliminar transacción (revirtiendo saldo)
    @Procedure(name = "sp_eliminar_transaccion")
    void eliminarTransaccion(@Param("p_transaction_id") Integer transactionId);

    // SP: Editar transacción
    @Procedure(name = "sp_editar_transaccion")
    void editarTransaccion(
        @Param("p_transaction_id") Integer transactionId,
        @Param("p_category_id") Integer categoryId,
        @Param("p_amount") BigDecimal amount,
        @Param("p_note") String note
    );

    // SP: Sincronizar saldos
    @Procedure(name = "sp_sincronizar_saldos")
    void sincronizarSaldos(@Param("p_user_id") Integer userId);

    // SP: Exportar reporte
    @Procedure(name = "sp_exportar_reporte")
    List<Object[]> exportarReporte(
        @Param("p_user_id") Integer userId,
        @Param("p_mes") Integer mes,
        @Param("p_anio") Integer anio
    );

    // SP: Obtener tipo de cambio actual
    @Procedure(name = "sp_obtener_tipo_cambio")
    List<Object[]> obtenerTipoCambio(@Param("p_user_id") Integer userId);

    // SP: Último tipo de cambio usado
    @Procedure(name = "sp_ultimo_tipo_cambio")
    List<Object[]> ultimoTipoCambio(@Param("p_user_id") Integer userId);
}