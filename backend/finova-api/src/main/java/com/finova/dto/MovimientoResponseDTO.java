
package com.finova.dto;

import com.finova.entity.Movimiento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoResponseDTO {

    private Long id;

    private Long cuentaOrigenId;
    private String cuentaOrigenNombre;

    private Long cuentaDestinoId;
    private String cuentaDestinoNombre;

    private Long metaId;
    private String metaNombre;

    private Long categoriaId;
    private String categoriaNombre;
    private String categoriaTipo;

    private Long metodoPagoId;
    private String metodoPagoNombre;

    private BigDecimal monto;
    private String titulo;
    private LocalDateTime fechaMovimiento;
    private String descripcion;
    private String tipoMovimiento;

    public static MovimientoResponseDTO desdeEntidad(Movimiento movimiento) {

        return new MovimientoResponseDTO(
                movimiento.getId(),

                movimiento.getCuentaOrigen() != null
                        ? movimiento.getCuentaOrigen().getId()
                        : null,

                movimiento.getCuentaOrigen() != null
                        ? movimiento.getCuentaOrigen().getNombre()
                        : null,

                movimiento.getCuentaDestino() != null
                        ? movimiento.getCuentaDestino().getId()
                        : null,

                movimiento.getCuentaDestino() != null
                        ? movimiento.getCuentaDestino().getNombre()
                        : null,

                movimiento.getMeta() != null
                        ? movimiento.getMeta().getId()
                        : null,

                movimiento.getMeta() != null
                        ? movimiento.getMeta().getNombre()
                        : null,

                movimiento.getCategoria() != null
                        ? movimiento.getCategoria().getId()
                        : null,

                movimiento.getCategoria() != null
                        ? movimiento.getCategoria().getNombre()
                        : null,

                movimiento.getCategoria() != null
                        ? movimiento.getCategoria().getTipo()
                        : null,

                movimiento.getMetodoPago() != null
                        ? movimiento.getMetodoPago().getId()
                        : null,

                movimiento.getMetodoPago() != null
                        ? movimiento.getMetodoPago().getNombre()
                        : null,

                movimiento.getMonto(),
                movimiento.getTitulo(),
                movimiento.getFechaMovimiento(),
                movimiento.getDescripcion(),

                movimiento.getTipoMovimiento() != null
                        ? movimiento.getTipoMovimiento().name()
                        : null
        );
    }
}
