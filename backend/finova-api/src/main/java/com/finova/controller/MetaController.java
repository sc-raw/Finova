
package com.finova.controller;

import com.finova.dto.MetaResponseDTO;
import com.finova.entity.Meta;
import com.finova.exception.ReglaNegocioException;
import com.finova.service.MetaService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/metas")
@RequiredArgsConstructor
public class MetaController {

    private final MetaService metaService;

    @GetMapping
    public ResponseEntity<List<MetaResponseDTO>> listarMetas() {
        List<MetaResponseDTO> lista = metaService.listarMetas()
                .stream()
                .map(MetaResponseDTO::desdeEntidad)
                .toList();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<MetaResponseDTO>> listarPorUsuario(
            @PathVariable Long usuarioId) {

        List<MetaResponseDTO> lista = metaService
                .listarPorUsuario(usuarioId)
                .stream()
                .map(MetaResponseDTO::desdeEntidad)
                .toList();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetaResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                MetaResponseDTO.desdeEntidad(
                        metaService.buscarPorId(id)
                )
        );
    }

    @GetMapping("/{id}/progreso")
    public ResponseEntity<BigDecimal> calcularProgreso(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                metaService.calcularProgreso(id)
        );
    }

    @PostMapping
    public ResponseEntity<MetaResponseDTO> guardar(
            @RequestBody Meta meta) {

        Meta guardada = metaService.guardar(meta);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(MetaResponseDTO.desdeEntidad(guardada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetaResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody Meta meta) {

        Meta actualizada = metaService.actualizar(id, meta);

        return ResponseEntity.ok(
                MetaResponseDTO.desdeEntidad(actualizada)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        metaService.eliminar(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/aportar")
    public ResponseEntity<Void> aportar(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> body) {

        metaService.aportar(id, obtenerMonto(body));

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/retirar")
    public ResponseEntity<Void> retirar(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> body) {

        metaService.retirar(id, obtenerMonto(body));

        return ResponseEntity.noContent().build();
    }

    private BigDecimal obtenerMonto(Map<String, BigDecimal> body) {
        if (body == null || body.get("monto") == null) {
            throw new ReglaNegocioException(
                    "El campo 'monto' es obligatorio"
            );
        }

        return body.get("monto");
    }
}
