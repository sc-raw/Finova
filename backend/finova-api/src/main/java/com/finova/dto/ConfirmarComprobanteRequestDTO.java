
package com.finova.dto;

import com.finova.entity.TipoMovimiento;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ConfirmarComprobanteRequestDTO {

    private TipoMovimiento tipoMovimiento;

    private Long cuentaOrigenId;
    private Long cuentaDestinoId;

    private Long categoriaId;
    private Long metodoPagoId;

    private BigDecimal monto;
    private LocalDateTime fechaMovimiento;
    private String descripcion;

    private String nombreComercio;
    private String ruc;
    private String codigoOperacion;
    private String rutaComprobante;
}
