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

    @GetMapping("/{id}")
    public ResponseEntity<MetodoPago> buscarPorId(@PathVariable Long id) {
        return metodoPagoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MetodoPago> guardar(@RequestBody MetodoPago metodoPago) {
        return ResponseEntity.ok(metodoPagoService.guardar(metodoPago));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        metodoPagoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}