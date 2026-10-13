
package com.finova.repository;

import com.finova.dto.EstadisticasResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class EstadisticasStoredProcedureRepository {

    private final JdbcTemplate jdbcTemplate;

    public EstadisticasResponseDTO ejecutar(
            Long usuarioId,
            LocalDateTime inicio,
            LocalDateTime fin
    ) {
        return jdbcTemplate.execute(
                (ConnectionCallback<EstadisticasResponseDTO>) connection -> {

                    try (CallableStatement cs = connection.prepareCall(
                            "{CALL sp_estadisticas_movimientos(?, ?, ?)}")) {

                        cs.setLong(1, usuarioId);

                        if (inicio == null) {
                            cs.setNull(2, Types.TIMESTAMP);
                        } else {
                            cs.setTimestamp(
                                    2, java.sql.Timestamp.valueOf(inicio));
                        }

                        if (fin == null) {
                            cs.setNull(3, Types.TIMESTAMP);
                        } else {
                            cs.setTimestamp(
                                    3, java.sql.Timestamp.valueOf(fin));
                        }

                        BigDecimal ingresos = BigDecimal.ZERO;
                        BigDecimal gastos = BigDecimal.ZERO;
                        BigDecimal ingresosAnterior = BigDecimal.ZERO;
                        BigDecimal gastosAnterior = BigDecimal.ZERO;

                        List<EstadisticasResponseDTO.CategoriaTotalDTO>
                                categorias = new ArrayList<>();

                        List<EstadisticasResponseDTO.EvolucionMensualDTO>
                                evolucion = new ArrayList<>();

                        boolean hayResultado = cs.execute();
                        int numeroResultado = 0;

                        while (true) {
                            if (hayResultado) {
                                try (ResultSet rs = cs.getResultSet()) {

                                    if (numeroResultado == 0 && rs.next()) {
                                        ingresos = rs.getBigDecimal(
                                                "total_ingresos");
                                        gastos = rs.getBigDecimal(
                                                "total_gastos");
                                        ingresosAnterior = rs.getBigDecimal(
                                                "ingresos_anterior");
                                        gastosAnterior = rs.getBigDecimal(
                                                "gastos_anterior");

                                    } else if (numeroResultado == 1) {
                                        while (rs.next()) {
                                            categorias.add(
                                                new EstadisticasResponseDTO
                                                    .CategoriaTotalDTO(
                                                        rs.getString("categoria"),
                                                        null,
                                                        null,
                                                        rs.getBigDecimal("total"),
                                                        rs.getBigDecimal("porcentaje")
                                                    )
                                            );
                                        }

                                    } else if (numeroResultado == 2) {
                                        while (rs.next()) {
                                            evolucion.add(
                                                new EstadisticasResponseDTO
                                                    .EvolucionMensualDTO(
                                                        rs.getString("mes"),
                                                        rs.getBigDecimal("ingresos"),
                                                        rs.getBigDecimal("gastos")
                                                    )
                                            );
                                        }
                                    }
                                }

                                numeroResultado++;

                            } else if (cs.getUpdateCount() == -1) {
                                break;
                            }

                            hayResultado = cs.getMoreResults();
                        }

                        BigDecimal tasaAhorro = BigDecimal.ZERO;

                        if (ingresos.compareTo(BigDecimal.ZERO) > 0) {
                            tasaAhorro = ingresos.subtract(gastos)
                                    .multiply(new BigDecimal("100"))
                                    .divide(
                                            ingresos,
                                            1,
                                            java.math.RoundingMode.HALF_UP
                                    );
                        }

                        return new EstadisticasResponseDTO(
                                ingresos,
                                gastos,
                                tasaAhorro,
                                ingresosAnterior,
                                gastosAnterior,
                                categorias,
                                evolucion
                        );
                    }
                }
        );
    }
}
