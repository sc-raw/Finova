package com.finova.service.imp;

import com.finova.entity.Categoria;
import com.finova.entity.Comprobante;
import com.finova.entity.Cuenta;
import com.finova.entity.Meta;
import com.finova.entity.MetodoPago;
import com.finova.entity.Movimiento;
import com.finova.entity.TipoMovimiento;
import com.finova.exception.RecursoNoEncontradoException;
import com.finova.exception.ReglaNegocioException;
import com.finova.repository.CategoriaRepository;
import com.finova.repository.ComprobanteRepository;
import com.finova.repository.CuentaRepository;
import com.finova.repository.MetaRepository;
import com.finova.repository.MetodoPagoRepository;
import com.finova.repository.MovimientoRepository;
import com.finova.service.MovimientoService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MovimientoServiceImp implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    private final CategoriaRepository categoriaRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final MetaRepository metaRepository;
    private final ComprobanteRepository comprobanteRepository;


    // =========================================================
    // LISTAR TODOS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> listarMovimientos() {
        return movimientoRepository.findAll()
                .stream()
                .sorted((a, b) -> {
                    if (a.getFechaMovimiento() == null) {
                        return b.getFechaMovimiento() == null ? 0 : 1;
                    }

                    if (b.getFechaMovimiento() == null) {
                        return -1;
                    }

                    return b.getFechaMovimiento()
                            .compareTo(a.getFechaMovimiento());
                })
                .toList();
    }


    // =========================================================
    // LISTAR POR USUARIO
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> listarPorUsuario(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new ReglaNegocioException(
                    "El ID del usuario debe ser válido"
            );
        }

        return movimientoRepository.findAll()
                .stream()
                .filter(movimiento -> {
                    boolean cuentaOrigenPertenece =
                            movimiento.getCuentaOrigen() != null
                            && movimiento.getCuentaOrigen().getUsuario() != null
                            && usuarioId.equals(
                                    movimiento.getCuentaOrigen()
                                            .getUsuario().getId()
                            );

                    boolean cuentaDestinoPertenece =
                            movimiento.getCuentaDestino() != null
                            && movimiento.getCuentaDestino().getUsuario() != null
                            && usuarioId.equals(
                                    movimiento.getCuentaDestino()
                                            .getUsuario().getId()
                            );

                    return cuentaOrigenPertenece || cuentaDestinoPertenece;
                })
                .sorted((a, b) -> {
                    if (a.getFechaMovimiento() == null) {
                        return b.getFechaMovimiento() == null ? 0 : 1;
                    }

                    if (b.getFechaMovimiento() == null) {
                        return -1;
                    }

                    return b.getFechaMovimiento()
                            .compareTo(a.getFechaMovimiento());
                })
                .toList();
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Movimiento buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new ReglaNegocioException(
                    "El ID del movimiento debe ser válido"
            );
        }

        return movimientoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Movimiento no encontrado con ID: " + id
                        )
                );
    }

    // =========================================================
    // GUARDAR
    // =========================================================

    @Override
    public Movimiento guardar(Movimiento movimiento) {
        if (movimiento == null) {
            throw new ReglaNegocioException(
                    "El movimiento es obligatorio"
            );
        }

        if (movimiento.getTipoMovimiento() == null) {
            throw new ReglaNegocioException(
                    "El tipo de movimiento es obligatorio"
            );
        }

        validarMonto(movimiento.getMonto());

        if (movimiento.getFechaMovimiento() == null) {
            movimiento.setFechaMovimiento(LocalDateTime.now());
        }

        aplicarMovimiento(movimiento);

        return movimientoRepository.save(movimiento);
    }


    // =========================================================
    // ACTUALIZAR
    // =========================================================

    @Override
    public Movimiento actualizar(Long id, Movimiento movimiento) {
        if (movimiento == null) {
            throw new ReglaNegocioException("El movimiento es obligatorio");
        }

        Movimiento existente = buscarPorId(id);

        if (movimiento.getTipoMovimiento() == null) {
            throw new ReglaNegocioException(
                    "El tipo de movimiento es obligatorio"
            );
        }

        validarMonto(movimiento.getMonto());

        Categoria categoria = null;

        if (movimiento.getCategoria() != null
                && movimiento.getCategoria().getId() != null) {
            categoria = categoriaRepository.findById(
                    movimiento.getCategoria().getId()
            ).orElseThrow(() ->
                    new RecursoNoEncontradoException(
                            "Categoría no encontrada"
                    )
            );
        }

        validarCategoriaParaMovimiento(
                categoria,
                movimiento.getTipoMovimiento()
        );

        MetodoPago metodoPago = null;

        if (movimiento.getMetodoPago() != null
                && movimiento.getMetodoPago().getId() != null) {
            metodoPago = metodoPagoRepository.findById(
                    movimiento.getMetodoPago().getId()
            ).orElseThrow(() ->
                    new RecursoNoEncontradoException(
                            "Método de pago no encontrado"
                    )
            );
        }

        revertirMovimiento(existente);

        existente.setDescripcion(movimiento.getDescripcion());
        existente.setTitulo(movimiento.getTitulo());
        existente.setFechaMovimiento(
                movimiento.getFechaMovimiento() != null
                        ? movimiento.getFechaMovimiento()
                        : LocalDateTime.now()
        );
        existente.setCategoria(categoria);
        existente.setMetodoPago(metodoPago);
        existente.setMonto(movimiento.getMonto());
        existente.setTipoMovimiento(movimiento.getTipoMovimiento());

        existente.setCuentaOrigen(
                movimiento.getCuentaOrigen() != null
                        && movimiento.getCuentaOrigen().getId() != null
                        ? obtenerCuenta(movimiento.getCuentaOrigen())
                        : null
        );

        existente.setCuentaDestino(
                movimiento.getCuentaDestino() != null
                        && movimiento.getCuentaDestino().getId() != null
                        ? obtenerCuenta(movimiento.getCuentaDestino())
                        : null
        );

        existente.setMeta(
                movimiento.getMeta() != null
                        && movimiento.getMeta().getId() != null
                        ? obtenerMeta(movimiento.getMeta())
                        : null
        );

        aplicarMovimiento(existente);

        return movimientoRepository.save(existente);
    }

    // =========================================================
    // ELIMINAR
    // =========================================================

    @Override
    @Transactional
    public void eliminar(Long id) {
        Movimiento movimiento = buscarPorId(id);

        Comprobante comprobante =
                comprobanteRepository.findByMovimientoId(id);

        revertirMovimiento(movimiento);

        if (comprobante != null) {
            comprobanteRepository.delete(comprobante);
        }

        movimientoRepository.delete(movimiento);
    }


    // =========================================================
    // REGISTRAR GASTO
    // =========================================================

    @Override
    public void registrarGasto(
            Long cuentaId,
            Long categoriaId,
            Long metodoPagoId,
            BigDecimal monto,
            String titulo,
            String descripcion) {

        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Cuenta no encontrada con ID: " + cuentaId
                        )
                );

        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Categoría no encontrada con ID: " + categoriaId
                        )
                );

        MetodoPago metodoPago = metodoPagoRepository.findById(metodoPagoId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Método de pago no encontrado con ID: " + metodoPagoId
                        )
                );

        validarMonto(monto);
        validarCategoriaParaMovimiento(categoria, TipoMovimiento.GASTO);
        validarSaldo(cuenta, monto);

        cuenta.setSaldoActual(cuenta.getSaldoActual().subtract(monto));

        Movimiento movimiento = new Movimiento();
        movimiento.setCuentaOrigen(cuenta);
        movimiento.setCategoria(categoria);
        movimiento.setMetodoPago(metodoPago);
        movimiento.setMonto(monto);
        movimiento.setTitulo(titulo);
        movimiento.setDescripcion(descripcion);
        movimiento.setTipoMovimiento(TipoMovimiento.GASTO);
        movimiento.setFechaMovimiento(LocalDateTime.now());

        cuentaRepository.save(cuenta);
        movimientoRepository.save(movimiento);
    }

    // =========================================================
    // REGISTRAR INGRESO
    // =========================================================

    @Override
    public void registrarIngreso(
            Long cuentaId,
            Long categoriaId,
            Long metodoPagoId,
            BigDecimal monto,
            String titulo,
            String descripcion) {

        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Cuenta no encontrada con ID: " + cuentaId
                        )
                );

        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Categoría no encontrada con ID: " + categoriaId
                        )
                );

        MetodoPago metodoPago = metodoPagoRepository.findById(metodoPagoId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Método de pago no encontrado con ID: " + metodoPagoId
                        )
                );

        validarMonto(monto);
        validarCategoriaParaMovimiento(categoria, TipoMovimiento.INGRESO);

        cuenta.setSaldoActual(cuenta.getSaldoActual().add(monto));

        Movimiento movimiento = new Movimiento();
        movimiento.setCuentaDestino(cuenta);
        movimiento.setCategoria(categoria);
        movimiento.setMetodoPago(metodoPago);
        movimiento.setMonto(monto);
        movimiento.setTitulo(titulo);
        movimiento.setDescripcion(descripcion);
        movimiento.setTipoMovimiento(TipoMovimiento.INGRESO);
        movimiento.setFechaMovimiento(LocalDateTime.now());

        cuentaRepository.save(cuenta);
        movimientoRepository.save(movimiento);
    }


	 // =========================================================
	 // TRANSFERENCIA
	 // =========================================================
	
	 @Override
	 @Transactional
	 public void transferir(
	         Long cuentaOrigenId,
	         Long cuentaDestinoId,
	         BigDecimal monto,
	         String descripcion) {
	
	     if (cuentaOrigenId == null || cuentaDestinoId == null) {
	         throw new ReglaNegocioException(
	                 "Las cuentas de origen y destino son obligatorias"
	         );
	     }
	
	     if (cuentaOrigenId.equals(cuentaDestinoId)) {
	         throw new ReglaNegocioException(
	                 "Las cuentas deben ser diferentes"
	         );
	     }
	
	     validarMonto(monto);
	
	     Cuenta origen = cuentaRepository.findById(cuentaOrigenId)
	             .orElseThrow(() ->
	                     new RecursoNoEncontradoException(
	                             "Cuenta de origen no encontrada"
	                     )
	             );
	
	     Cuenta destino = cuentaRepository.findById(cuentaDestinoId)
	             .orElseThrow(() ->
	                     new RecursoNoEncontradoException(
	                             "Cuenta de destino no encontrada"
	                     )
	             );
	
	     validarSaldo(origen, monto);
	
	     origen.setSaldoActual(origen.getSaldoActual().subtract(monto));
	     destino.setSaldoActual(destino.getSaldoActual().add(monto));
	
	     Movimiento movimiento = new Movimiento();
	     movimiento.setCuentaOrigen(origen);
	     movimiento.setCuentaDestino(destino);
	     movimiento.setMonto(monto);
	     movimiento.setTitulo("Transferencia entre cuentas");
	     movimiento.setDescripcion(descripcion);
	     movimiento.setTipoMovimiento(TipoMovimiento.TRANSFERENCIA);
	     movimiento.setFechaMovimiento(LocalDateTime.now());
	
	     cuentaRepository.save(origen);
	     cuentaRepository.save(destino);
	     movimientoRepository.save(movimiento);
	 }

	// =========================================================
	// APORTE A META
	// =========================================================

	@Override
	@Transactional
	public void aportarMeta(
	        Long cuentaId,
	        Long metaId,
	        BigDecimal monto,
	        String descripcion) {

	    if (cuentaId == null || metaId == null) {
	        throw new ReglaNegocioException(
	                "La cuenta y la meta son obligatorias"
	        );
	    }

	    validarMonto(monto);

	    Cuenta cuenta = cuentaRepository.findById(cuentaId)
	            .orElseThrow(() ->
	                    new RecursoNoEncontradoException(
	                            "Cuenta no encontrada"
	                    )
	            );

	    Meta meta = metaRepository.findById(metaId)
	            .orElseThrow(() ->
	                    new RecursoNoEncontradoException(
	                            "Meta no encontrada"
	                    )
	            );

	    Movimiento movimiento = new Movimiento();
	    movimiento.setCuentaOrigen(cuenta);
	    movimiento.setMeta(meta);
	    movimiento.setMonto(monto);
	    movimiento.setTitulo("Aporte a meta");
	    movimiento.setDescripcion(descripcion);
	    movimiento.setTipoMovimiento(TipoMovimiento.APORTE_META);
	    movimiento.setFechaMovimiento(LocalDateTime.now());

	    guardar(movimiento);
	}







    @Override
    public Movimiento confirmarComprobante(
            com.finova.dto.ConfirmarComprobanteRequestDTO request) {

        if (request == null) {
            throw new ReglaNegocioException(
                    "Los datos del comprobante son obligatorios"
            );
        }


        if (request.getCodigoOperacion() != null
                && !request.getCodigoOperacion().isBlank()
                && comprobanteRepository.existsByCodigoOperacion(
                        request.getCodigoOperacion().trim())) {

            throw new ReglaNegocioException(
                    "Este comprobante ya fue registrado"
            );
        }

        if (request.getTipoMovimiento() == null) {
            throw new ReglaNegocioException(
                    "El tipo de movimiento es obligatorio"
            );
        }

        Movimiento movimiento = new Movimiento();

        movimiento.setTipoMovimiento(request.getTipoMovimiento());
        movimiento.setMonto(request.getMonto());
        movimiento.setFechaMovimiento(request.getFechaMovimiento());
        movimiento.setTitulo(request.getNombreComercio());
        movimiento.setDescripcion(request.getDescripcion());

        if (request.getCuentaOrigenId() != null) {
            Cuenta cuentaOrigen = cuentaRepository.findById(
                    request.getCuentaOrigenId()
            ).orElseThrow(() -> new RecursoNoEncontradoException(
                    "Cuenta de origen no encontrada"
            ));

            movimiento.setCuentaOrigen(cuentaOrigen);
        }

        if (request.getCuentaDestinoId() != null) {
            Cuenta cuentaDestino = cuentaRepository.findById(
                    request.getCuentaDestinoId()
            ).orElseThrow(() -> new RecursoNoEncontradoException(
                    "Cuenta de destino no encontrada"
            ));

            movimiento.setCuentaDestino(cuentaDestino);
        }

        if (request.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(
                    request.getCategoriaId()
            ).orElseThrow(() -> new RecursoNoEncontradoException(
                    "Categoría no encontrada"
            ));

            movimiento.setCategoria(categoria);
        }

        if (request.getMetodoPagoId() != null) {
            MetodoPago metodoPago = metodoPagoRepository.findById(
                    request.getMetodoPagoId()
            ).orElseThrow(() -> new RecursoNoEncontradoException(
                    "Método de pago no encontrado"
            ));

            movimiento.setMetodoPago(metodoPago);
        }

        Movimiento guardado = guardar(movimiento);

        Comprobante comprobante = new Comprobante();
        comprobante.setMovimiento(guardado);
        comprobante.setNombreComercio(request.getNombreComercio());
        comprobante.setRuc(request.getRuc());
        comprobante.setCodigoOperacion(request.getCodigoOperacion());
        comprobante.setRutaComprobante(request.getRutaComprobante());

        comprobanteRepository.save(comprobante);

        return guardado;
    }



    // =========================================================
    // LISTAR POR TIPO Y USUARIO
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> listarPorTipoYUsuario(
            String tipo,
            Long usuarioId) {

        if (tipo == null || tipo.isBlank()) {
            throw new ReglaNegocioException(
                    "El tipo de movimiento es obligatorio"
            );
        }

        TipoMovimiento tipoMovimiento;

        try {
            tipoMovimiento = TipoMovimiento.valueOf(
                    tipo.trim().toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new ReglaNegocioException(
                    "Tipo de movimiento no válido: " + tipo
            );
        }

        return listarPorUsuario(usuarioId)
                .stream()
                .filter(movimiento ->
                        movimiento.getTipoMovimiento() == tipoMovimiento
                )
                .toList();
    }


    // =========================================================
    // LISTAR POR CUENTA
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> listarPorCuenta(Long cuentaId) {
        if (cuentaId == null || cuentaId <= 0) {
            throw new ReglaNegocioException(
                    "El ID de la cuenta debe ser válido"
            );
        }

        return movimientoRepository.findAll()
                .stream()
                .filter(movimiento ->
                        (movimiento.getCuentaOrigen() != null
                                && cuentaId.equals(
                                        movimiento.getCuentaOrigen().getId()
                                ))
                        ||
                        (movimiento.getCuentaDestino() != null
                                && cuentaId.equals(
                                        movimiento.getCuentaDestino().getId()
                                ))
                )
                .sorted((a, b) -> {
                    if (a.getFechaMovimiento() == null) {
                        return b.getFechaMovimiento() == null ? 0 : 1;
                    }

                    if (b.getFechaMovimiento() == null) {
                        return -1;
                    }

                    return b.getFechaMovimiento()
                            .compareTo(a.getFechaMovimiento());
                })
                .toList();
    }


    // =========================================================
    // REVERTIR MOVIMIENTO
    // =========================================================

    private void revertirMovimiento(Movimiento movimiento) {
        if (movimiento == null) {
            throw new ReglaNegocioException(
                    "El movimiento es obligatorio"
            );
        }

        BigDecimal monto = movimiento.getMonto();
        validarMonto(monto);

        switch (movimiento.getTipoMovimiento()) {
            case GASTO -> {
                Cuenta cuenta = obtenerCuenta(movimiento.getCuentaOrigen());
                cuenta.setSaldoActual(cuenta.getSaldoActual().add(monto));
                cuentaRepository.save(cuenta);
            }

            case INGRESO -> {
                Cuenta cuenta = obtenerCuenta(movimiento.getCuentaDestino());

                if (cuenta.getSaldoActual().compareTo(monto) < 0) {
                    throw new ReglaNegocioException(
                            "No se puede revertir el ingreso porque el saldo es insuficiente"
                    );
                }

                cuenta.setSaldoActual(cuenta.getSaldoActual().subtract(monto));
                cuentaRepository.save(cuenta);
            }

            case TRANSFERENCIA -> {
                Cuenta origen = obtenerCuenta(movimiento.getCuentaOrigen());
                Cuenta destino = obtenerCuenta(movimiento.getCuentaDestino());

                if (origen.getId().equals(destino.getId())) {
                    throw new ReglaNegocioException(
                            "Las cuentas de origen y destino deben ser diferentes"
                    );
                }

                if (destino.getSaldoActual().compareTo(monto) < 0) {
                    throw new ReglaNegocioException(
                            "No se puede revertir la transferencia porque el saldo destino es insuficiente"
                    );
                }

                origen.setSaldoActual(origen.getSaldoActual().add(monto));
                destino.setSaldoActual(destino.getSaldoActual().subtract(monto));

                cuentaRepository.save(origen);
                cuentaRepository.save(destino);
            }

            case APORTE_META -> {
                Cuenta cuenta = obtenerCuenta(movimiento.getCuentaOrigen());
                Meta meta = obtenerMeta(movimiento.getMeta());

                if (meta.getMontoActual().compareTo(monto) < 0) {
                    throw new ReglaNegocioException(
                            "No se puede revertir el aporte porque el saldo de la meta es insuficiente"
                    );
                }

                cuenta.setSaldoActual(cuenta.getSaldoActual().add(monto));
                meta.setMontoActual(meta.getMontoActual().subtract(monto));

                cuentaRepository.save(cuenta);
                metaRepository.save(meta);
            }

            case RETIRO_META -> {
                Cuenta cuenta = obtenerCuenta(movimiento.getCuentaDestino());
                Meta meta = obtenerMeta(movimiento.getMeta());

                if (cuenta.getSaldoActual().compareTo(monto) < 0) {
                    throw new ReglaNegocioException(
                            "No se puede revertir el retiro porque el saldo de la cuenta es insuficiente"
                    );
                }

                cuenta.setSaldoActual(cuenta.getSaldoActual().subtract(monto));
                meta.setMontoActual(meta.getMontoActual().add(monto));

                cuentaRepository.save(cuenta);
                metaRepository.save(meta);
            }
        }
    }


    // =========================================================
    // APLICAR MOVIMIENTO
    // =========================================================

    private void aplicarMovimiento(Movimiento movimiento) {
        if (movimiento == null) {
            throw new ReglaNegocioException(
                    "El movimiento es obligatorio"
            );
        }

        if (movimiento.getTipoMovimiento() == null) {
            throw new ReglaNegocioException(
                    "El tipo de movimiento es obligatorio"
            );
        }

        BigDecimal monto = movimiento.getMonto();
        validarMonto(monto);

        switch (movimiento.getTipoMovimiento()) {
            case GASTO -> {
                Categoria categoria = movimiento.getCategoria();
                validarCategoriaParaMovimiento(
                        categoria != null && categoria.getId() != null
                                ? categoriaRepository.findById(categoria.getId())
                                        .orElseThrow(() ->
                                                new RecursoNoEncontradoException(
                                                        "Categoría no encontrada"
                                                ))
                                : null,
                        TipoMovimiento.GASTO
                );

                Cuenta cuenta = obtenerCuenta(movimiento.getCuentaOrigen());
                validarSaldo(cuenta, monto);

                cuenta.setSaldoActual(cuenta.getSaldoActual().subtract(monto));
                cuentaRepository.save(cuenta);
                movimiento.setCuentaOrigen(cuenta);
            }

            case INGRESO -> {
                Categoria categoria = movimiento.getCategoria();
                validarCategoriaParaMovimiento(
                        categoria != null && categoria.getId() != null
                                ? categoriaRepository.findById(categoria.getId())
                                        .orElseThrow(() ->
                                                new RecursoNoEncontradoException(
                                                        "Categoría no encontrada"
                                                ))
                                : null,
                        TipoMovimiento.INGRESO
                );

                Cuenta cuenta = obtenerCuenta(movimiento.getCuentaDestino());
                cuenta.setSaldoActual(cuenta.getSaldoActual().add(monto));
                cuentaRepository.save(cuenta);
                movimiento.setCuentaDestino(cuenta);
            }

            case TRANSFERENCIA -> {
                Cuenta origen = obtenerCuenta(movimiento.getCuentaOrigen());
                Cuenta destino = obtenerCuenta(movimiento.getCuentaDestino());

                if (origen.getId().equals(destino.getId())) {
                    throw new ReglaNegocioException(
                            "Las cuentas de origen y destino deben ser diferentes"
                    );
                }

                validarSaldo(origen, monto);

                origen.setSaldoActual(origen.getSaldoActual().subtract(monto));
                destino.setSaldoActual(destino.getSaldoActual().add(monto));

                cuentaRepository.save(origen);
                cuentaRepository.save(destino);

                movimiento.setCuentaOrigen(origen);
                movimiento.setCuentaDestino(destino);
            }

            case APORTE_META -> {
                Cuenta cuenta = obtenerCuenta(movimiento.getCuentaOrigen());
                Meta meta = obtenerMeta(movimiento.getMeta());

                validarSaldo(cuenta, monto);

                if (meta.getMontoActual() == null
                        || meta.getMontoObjetivo() == null) {
                    throw new ReglaNegocioException(
                            "La meta no tiene sus montos correctamente configurados"
                    );
                }

                BigDecimal nuevoMonto = meta.getMontoActual().add(monto);

                if (nuevoMonto.compareTo(meta.getMontoObjetivo()) > 0) {
                    throw new ReglaNegocioException(
                            "El aporte supera el monto objetivo de la meta"
                    );
                }

                cuenta.setSaldoActual(cuenta.getSaldoActual().subtract(monto));
                meta.setMontoActual(nuevoMonto);

                cuentaRepository.save(cuenta);
                metaRepository.save(meta);

                movimiento.setCuentaOrigen(cuenta);
                movimiento.setMeta(meta);
            }

            case RETIRO_META -> {
                Meta meta = obtenerMeta(movimiento.getMeta());
                Cuenta cuenta = obtenerCuenta(movimiento.getCuentaDestino());

                if (meta.getMontoActual() == null
                        || meta.getMontoActual().compareTo(monto) < 0) {
                    throw new ReglaNegocioException(
                            "La meta no tiene saldo suficiente"
                    );
                }

                meta.setMontoActual(meta.getMontoActual().subtract(monto));
                cuenta.setSaldoActual(cuenta.getSaldoActual().add(monto));

                metaRepository.save(meta);
                cuentaRepository.save(cuenta);

                movimiento.setMeta(meta);
                movimiento.setCuentaDestino(cuenta);
            }
        }
    }


    // =========================================================
    // OBTENER CUENTA
    // =========================================================

    private Cuenta obtenerCuenta(Cuenta cuenta) {
        if (cuenta == null || cuenta.getId() == null) {
            throw new ReglaNegocioException(
                    "La cuenta es obligatoria"
            );
        }

        if (cuenta.getId() <= 0) {
            throw new ReglaNegocioException(
                    "El ID de la cuenta debe ser válido"
            );
        }

        return cuentaRepository.findById(cuenta.getId())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Cuenta no encontrada con ID: "
                                        + cuenta.getId()
                        )
                );
    }


    // =========================================================
    // OBTENER META
    // =========================================================

    private Meta obtenerMeta(Meta meta) {
        if (meta == null || meta.getId() == null) {
            throw new ReglaNegocioException(
                    "La meta es obligatoria"
            );
        }

        if (meta.getId() <= 0) {
            throw new ReglaNegocioException(
                    "El ID de la meta debe ser válido"
            );
        }

        return metaRepository.findById(meta.getId())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Meta no encontrada con ID: "
                                        + meta.getId()
                        )
                );
    }


    // =========================================================
    // VALIDAR MONTO
    // =========================================================

    private void validarMonto(BigDecimal monto) {
        if (monto == null) {
            throw new ReglaNegocioException(
                    "El monto es obligatorio"
            );
        }

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException(
                    "El monto debe ser mayor a cero"
            );
        }

        if (monto.scale() > 2) {
            throw new ReglaNegocioException(
                    "El monto no puede tener más de dos decimales"
            );
        }
    }

    private void validarCategoriaParaMovimiento(
            Categoria categoria,
            TipoMovimiento tipoMovimiento) {

        if (tipoMovimiento == null) {
            throw new ReglaNegocioException(
                    "El tipo de movimiento es obligatorio"
            );
        }

        if (categoria == null) {
            if (tipoMovimiento == TipoMovimiento.GASTO
                    || tipoMovimiento == TipoMovimiento.INGRESO) {
                throw new ReglaNegocioException(
                        "La categoría es obligatoria para gastos e ingresos"
                );
            }

            return;
        }

        String tipoCategoria = categoria.getTipo();

        if (tipoCategoria == null || tipoCategoria.isBlank()) {
            throw new ReglaNegocioException(
                    "La categoría no tiene un tipo definido"
            );
        }

        if (tipoMovimiento == TipoMovimiento.GASTO
                && !"GASTO".equalsIgnoreCase(tipoCategoria.trim())) {
            throw new ReglaNegocioException(
                    "La categoría seleccionada no corresponde a un movimiento de GASTO"
            );
        }

        if (tipoMovimiento == TipoMovimiento.INGRESO
                && !"INGRESO".equalsIgnoreCase(tipoCategoria.trim())) {
            throw new ReglaNegocioException(
                    "La categoría seleccionada no corresponde a un movimiento de INGRESO"
            );
        }
    }



    // =========================================================
    // VALIDAR SALDO
    // =========================================================

    private void validarSaldo(Cuenta cuenta, BigDecimal monto) {
        if (cuenta == null) {
            throw new ReglaNegocioException(
                    "La cuenta es obligatoria"
            );
        }

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException(
                    "El monto debe ser mayor a cero"
            );
        }

        if (cuenta.getSaldoActual() == null) {
            throw new ReglaNegocioException(
                    "La cuenta no tiene un saldo inicializado"
            );
        }

        if (cuenta.getSaldoActual().compareTo(monto) < 0) {
            throw new ReglaNegocioException(
                    "Saldo insuficiente"
            );
        }
    }
}
