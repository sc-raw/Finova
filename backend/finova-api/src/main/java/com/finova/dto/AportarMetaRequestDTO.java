package com.finova.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class AportarMetaRequestDTO {
    private Long cuentaId;
    private Long metaId;
    private BigDecimal monto;
    private String descripcion;
}