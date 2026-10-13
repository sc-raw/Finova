
package com.finova.service.imp;

import com.finova.dto.EstadisticasResponseDTO;
import com.finova.repository.EstadisticasStoredProcedureRepository;
import com.finova.service.EstadisticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class EstadisticaServiceImp implements EstadisticaService {

    private final EstadisticasStoredProcedureRepository estadisticasRepository;

    @Override
    public EstadisticasResponseDTO calcular(Long usuarioId, String periodo) {

        LocalDateTime fin = LocalDateTime.now();
        LocalDateTime inicio = calcularInicio(fin, periodo);

        return estadisticasRepository.ejecutar(
                usuarioId,
                inicio,
                fin
        );
    }

    private LocalDateTime calcularInicio(
            LocalDateTime fin,
            String periodo
    ) {
        if (periodo == null) {
            return fin.minusMonths(1);
        }

        return switch (periodo.toLowerCase()) {
            case "semana" -> fin.minusWeeks(1);
            case "anio", "año" -> fin.minusYears(1);
            default -> fin.minusMonths(1);
        };
    }
}
