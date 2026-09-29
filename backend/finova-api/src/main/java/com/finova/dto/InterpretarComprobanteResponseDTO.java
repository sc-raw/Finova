package com.finova.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterpretarComprobanteResponseDTO {

    private String razonamiento;
    private String tipoDocumento;
    private String comercio;
    private String ruc;
    private String fecha;
    private String moneda;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private String categoriaSugerida;
    private BigDecimal confianza;
    private List<ItemDTO> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDTO {
        private String nombre;
        private Integer cantidad;
        private BigDecimal precio;
    }
}