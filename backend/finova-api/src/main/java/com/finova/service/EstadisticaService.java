package com.finova.service;

import com.finova.dto.EstadisticasResponseDTO;

public interface EstadisticaService {

    EstadisticasResponseDTO calcular(Long usuarioId, String periodo);
}