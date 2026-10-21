
package com.finova.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditarMovimientoRequestDTO {

    private String titulo;
    private BigDecimal monto;
    private Long categoriaId;
}
