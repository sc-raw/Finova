package com.finova.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class RegistrarGastoRequestDTO {
    private Long cuentaId;
    private Long categoriaId;
    private Long metodoPagoId;
    private BigDecimal monto;
    private String descripcion;
}