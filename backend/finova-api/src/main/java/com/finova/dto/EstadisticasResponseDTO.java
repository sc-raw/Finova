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
public class EstadisticasResponseDTO {

    private BigDecimal totalIngresos;
    private BigDecimal totalGastos;
    private BigDecimal tasaAhorro;
    private BigDecimal ingresosAnterior;
    private BigDecimal gastosAnterior;
    private List<CategoriaTotalDTO> distribucionCategorias;
    private List<EvolucionMensualDTO> evolucionMensual;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoriaTotalDTO {
        private String nombre;
        private String color;
        private String icon;
        private BigDecimal total;
        private BigDecimal porcentaje;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvolucionMensualDTO {
        private String mes;
        private BigDecimal ingresos;
        private BigDecimal gastos;
    }
}