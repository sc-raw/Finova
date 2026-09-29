package com.finova.service;

import com.finova.dto.EstadisticasResponseDTO;
import com.finova.entity.Categoria;
import com.finova.entity.Movimiento;
import com.finova.entity.TipoMovimiento;
import com.finova.repository.CategoriaRepository;
import com.finova.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstadisticaService {

    private final MovimientoRepository movimientoRepository;
    private final CategoriaRepository categoriaRepository;

    public EstadisticasResponseDTO calcular(Long usuarioId, String periodo) {
        LocalDateTime fin = LocalDateTime.now();
        LocalDateTime inicio = calcularInicio(fin, periodo);
        LocalDateTime inicioAnterior = inicio.minusDays(java.time.Duration.between(inicio, fin).toDays());
        LocalDateTime finAnterior = inicio;

        // Movimientos del periodo actual
        List<Movimiento> actuales = movimientoRepository
                .findByCuentaOrigenUsuarioIdOrderByFechaMovimientoDesc(usuarioId)
                .stream()
                .filter(m -> m.getFechaMovimiento().isAfter(inicio) && m.getFechaMovimiento().isBefore(fin))
                .collect(Collectors.toList());

        // Movimientos del periodo anterior
        List<Movimiento> anteriores = movimientoRepository
                .findByCuentaOrigenUsuarioIdOrderByFechaMovimientoDesc(usuarioId)
                .stream()
                .filter(m -> m.getFechaMovimiento().isAfter(inicioAnterior) && m.getFechaMovimiento().isBefore(finAnterior))
                .collect(Collectors.toList());

        BigDecimal totalIngresos = sumarPorTipo(actuales, TipoMovimiento.INGRESO);
        BigDecimal totalGastos = sumarPorTipo(actuales, TipoMovimiento.GASTO);
        BigDecimal ingresosAnterior = sumarPorTipo(anteriores, TipoMovimiento.INGRESO);
        BigDecimal gastosAnterior = sumarPorTipo(anteriores, TipoMovimiento.GASTO);

        BigDecimal tasaAhorro = BigDecimal.ZERO;
        if (totalIngresos.compareTo(BigDecimal.ZERO) > 0) {
            tasaAhorro = totalIngresos.subtract(totalGastos)
                    .multiply(new BigDecimal("100"))
                    .divide(totalIngresos, 1, RoundingMode.HALF_UP);
        }

        // Distribución por categoría
        Map<Long, BigDecimal> totalPorCategoria = new HashMap<>();
        for (Movimiento m : actuales) {
            if (m.getTipoMovimiento() == TipoMovimiento.GASTO && m.getCategoria() != null) {
                totalPorCategoria.merge(m.getCategoria().getId(), m.getMonto(), BigDecimal::add);
            }
        }

        List<EstadisticasResponseDTO.CategoriaTotalDTO> distribucion = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : totalPorCategoria.entrySet()) {
            Categoria cat = categoriaRepository.findById(entry.getKey()).orElse(null);
            if (cat != null) {
                BigDecimal porcentaje = totalGastos.compareTo(BigDecimal.ZERO) > 0
                        ? entry.getValue().multiply(new BigDecimal("100")).divide(totalGastos, 1, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;
                distribucion.add(new EstadisticasResponseDTO.CategoriaTotalDTO(
                        cat.getNombre(), null, null, entry.getValue(), porcentaje
                ));
            }
        }
        distribucion.sort((a, b) -> b.getTotal().compareTo(a.getTotal()));

        // Evolución mensual (últimos 6 meses)
        List<EstadisticasResponseDTO.EvolucionMensualDTO> evolucion = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);
            LocalDateTime mesInicio = ym.atDay(1).atStartOfDay();
            LocalDateTime mesFin = ym.atEndOfMonth().atTime(23, 59, 59);

            BigDecimal ingresosMes = BigDecimal.ZERO;
            BigDecimal gastosMes = BigDecimal.ZERO;
            for (Movimiento m : actuales) {
                if (m.getFechaMovimiento().isAfter(mesInicio) && m.getFechaMovimiento().isBefore(mesFin)) {
                    if (m.getTipoMovimiento() == TipoMovimiento.INGRESO) ingresosMes = ingresosMes.add(m.getMonto());
                    if (m.getTipoMovimiento() == TipoMovimiento.GASTO) gastosMes = gastosMes.add(m.getMonto());
                }
            }
            evolucion.add(new EstadisticasResponseDTO.EvolucionMensualDTO(
                    ym.format(formatter), ingresosMes, gastosMes
            ));
        }

        return new EstadisticasResponseDTO(
                totalIngresos, totalGastos, tasaAhorro,
                ingresosAnterior, gastosAnterior,
                distribucion, evolucion
        );
    }

    private BigDecimal sumarPorTipo(List<Movimiento> movimientos, TipoMovimiento tipo) {
        return movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == tipo)
                .map(Movimiento::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private LocalDateTime calcularInicio(LocalDateTime fin, String periodo) {
        return switch (periodo) {
            case "semana" -> fin.minusWeeks(1);
            case "anio" -> fin.minusYears(1);
            default -> fin.minusMonths(1); // "mes"
        };
    }
}