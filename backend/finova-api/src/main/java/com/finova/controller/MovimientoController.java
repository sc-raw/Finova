
package com.finova.controller;

import com.finova.dto.AportarMetaRequestDTO;
import com.finova.dto.MovimientoResponseDTO;
import com.finova.dto.RegistrarGastoRequestDTO;
import com.finova.dto.RegistrarIngresoRequestDTO;
import com.finova.dto.TransferirRequestDTO;
import com.finova.entity.Movimiento;
import com.finova.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.finova.dto.ConfirmarComprobanteRequestDTO;
import com.finova.dto.EditarMovimientoRequestDTO;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<MovimientoResponseDTO>> listarPorUsuario(
            @PathVariable Long usuarioId) {
        return ResponseEntity.ok(
                movimientoService.listarPorUsuario(usuarioId)
                        .stream()
                        .map(MovimientoResponseDTO::desdeEntidad)
                        .toList()
        );
    }

    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<List<MovimientoResponseDTO>> listarPorCuenta(
            @PathVariable Long cuentaId) {
        return ResponseEntity.ok(
                movimientoService.listarPorCuenta(cuentaId)
                        .stream()
                        .map(MovimientoResponseDTO::desdeEntidad)
                        .toList()
        );
    }
    
    @PatchMapping("/{id}/editar")
    public ResponseEntity<Void> editarMovimiento(
            @PathVariable Long id,
            @RequestBody EditarMovimientoRequestDTO request) {

        movimientoService.editarMovimiento(id, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoResponseDTO> buscarPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                MovimientoResponseDTO.desdeEntidad(
                        movimientoService.buscarPorId(id)
                )
        );
    }

    @PostMapping
    public ResponseEntity<MovimientoResponseDTO> guardar(
            @RequestBody Movimiento movimiento) {
        Movimiento guardado = movimientoService.guardar(movimiento);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MovimientoResponseDTO.desdeEntidad(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimientoResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody Movimiento movimiento) {
        return ResponseEntity.ok(
                MovimientoResponseDTO.desdeEntidad(
                        movimientoService.actualizar(id, movimiento)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        movimientoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/gasto")
    public ResponseEntity<Void> registrarGasto(
            @RequestBody RegistrarGastoRequestDTO request) {
    	movimientoService.registrarGasto(
    	        request.getCuentaId(),
    	        request.getCategoriaId(),
    	        request.getMetodoPagoId(),
    	        request.getMonto(),
    	        request.getMoneda(),
		        request.getTitulo(),
    	        request.getDescripcion()
    	);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/ingreso")
    public ResponseEntity<Void> registrarIngreso(
            @RequestBody RegistrarIngresoRequestDTO request) {
    	movimientoService.registrarIngreso(
    	        request.getCuentaId(),
    	        request.getCategoriaId(),
    	        request.getMetodoPagoId(),
    	        request.getMonto(),
    	        request.getMoneda(),
		        request.getTitulo(),
    	        request.getDescripcion()
    	);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/transferir")
    public ResponseEntity<Void> transferir(
            @RequestBody TransferirRequestDTO request) {
        movimientoService.transferir(
                request.getCuentaOrigenId(),
                request.getCuentaDestinoId(),
                request.getMonto(),
                request.getDescripcion()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/aportar-meta")
    public ResponseEntity<Void> aportarMeta(
            @RequestBody AportarMetaRequestDTO request) {
        movimientoService.aportarMeta(
                request.getCuentaId(),
                request.getMetaId(),
                request.getMonto(),
                request.getDescripcion()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/usuario/{usuarioId}/tipo/{tipo}")
    public ResponseEntity<List<MovimientoResponseDTO>> listarPorTipoYUsuario(
            @PathVariable String tipo,
            @PathVariable Long usuarioId) {
        return ResponseEntity.ok(
                movimientoService.listarPorTipoYUsuario(tipo, usuarioId)
                        .stream()
                        .map(MovimientoResponseDTO::desdeEntidad)
                        .toList()
        );
    }
    
    @PostMapping("/confirmar-comprobante")
    public ResponseEntity<MovimientoResponseDTO> confirmarComprobante(
            @RequestBody ConfirmarComprobanteRequestDTO request) {

        Movimiento guardado = movimientoService.confirmarComprobante(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MovimientoResponseDTO.desdeEntidad(guardado));
    }
    
}
