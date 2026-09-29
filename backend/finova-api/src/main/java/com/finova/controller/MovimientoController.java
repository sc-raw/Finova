package com.finova.controller;

import com.finova.dto.AportarMetaRequestDTO;
import com.finova.dto.RegistrarGastoRequestDTO;
import com.finova.dto.RegistrarIngresoRequestDTO;
import com.finova.dto.TransferirRequestDTO;
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

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Movimiento>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(movimientoService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<List<Movimiento>> listarPorCuenta(@PathVariable Long cuentaId) {
        return ResponseEntity.ok(movimientoService.listarPorCuenta(cuentaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movimiento> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Movimiento> guardar(@RequestBody Movimiento movimiento) {
        return ResponseEntity.ok(movimientoService.guardar(movimiento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movimiento> actualizar(@PathVariable Long id, @RequestBody Movimiento movimiento) {
        return ResponseEntity.ok(movimientoService.actualizar(id, movimiento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        movimientoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== OPERACIONES ====================

    @PostMapping("/gasto")
    public ResponseEntity<Void> registrarGasto(@RequestBody RegistrarGastoRequestDTO request) {
        movimientoService.registrarGasto(
                request.getCuentaId(),
                request.getCategoriaId(),
                request.getMetodoPagoId(),
                request.getMonto(),
                request.getDescripcion()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ingreso")
    public ResponseEntity<Void> registrarIngreso(@RequestBody RegistrarIngresoRequestDTO request) {
        movimientoService.registrarIngreso(
                request.getCuentaId(),
                request.getCategoriaId(),
                request.getMetodoPagoId(),
                request.getMonto(),
                request.getDescripcion()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transferir")
    public ResponseEntity<Void> transferir(@RequestBody TransferirRequestDTO request) {
        movimientoService.transferir(
                request.getCuentaOrigenId(),
                request.getCuentaDestinoId(),
                request.getMonto(),
                request.getDescripcion()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/aportar-meta")
    public ResponseEntity<Void> aportarMeta(@RequestBody AportarMetaRequestDTO request) {
        movimientoService.aportarMeta(
                request.getCuentaId(),
                request.getMetaId(),
                request.getMonto(),
                request.getDescripcion()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/usuario/{usuarioId}/tipo/{tipo}")
    public ResponseEntity<List<Movimiento>> listarPorTipoYUsuario(@PathVariable String tipo,
                                                                    @PathVariable Long usuarioId) {
        return ResponseEntity.ok(movimientoService.listarPorTipoYUsuario(tipo.toUpperCase(), usuarioId));
    }
}