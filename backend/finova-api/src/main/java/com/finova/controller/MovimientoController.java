package com.finova.controller;

import com.finova.entity.Movimiento;
import com.finova.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    @GetMapping
    public ResponseEntity<List<Movimiento>> listarMovimientos() {
        return ResponseEntity.ok(movimientoService.listarMovimientos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movimiento> buscarPorId(@PathVariable Long id) {
        return movimientoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Movimiento> guardar(@RequestBody Movimiento movimiento) {
        return ResponseEntity.ok(movimientoService.guardar(movimiento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        movimientoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}