package com.finova.controller;

import com.finova.entity.MetodoPago;
import com.finova.service.MetodoPagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metodos-pago")
@RequiredArgsConstructor
public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;

    @GetMapping
    public ResponseEntity<List<MetodoPago>> listarMetodosPago() {
        return ResponseEntity.ok(metodoPagoService.listarMetodosPago());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<MetodoPago>> listarActivos() {
        return ResponseEntity.ok(metodoPagoService.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetodoPago> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(metodoPagoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<MetodoPago> guardar(@RequestBody MetodoPago metodoPago) {
        return ResponseEntity.ok(metodoPagoService.guardar(metodoPago));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetodoPago> actualizar(@PathVariable Long id, @RequestBody MetodoPago metodoPago) {
        return ResponseEntity.ok(metodoPagoService.actualizar(id, metodoPago));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        metodoPagoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}