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

        return movimientoRepository.findAll();
    }


    // =========================================================
    // LISTAR POR USUARIO
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> listarPorUsuario(Long usuarioId) {

        return movimientoRepository
                .findByCuentaOrigenUsuarioIdOrderByFechaMovimientoDesc(usuarioId);
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Movimiento buscarPorId(Long id) {

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

        return movimientoRepository.save(movimiento);
    }


    // =========================================================
    // ACTUALIZAR
    // =========================================================

    @Override
    public Movimiento actualizar(Long id, Movimiento movimiento) {

        Movimiento existente = buscarPorId(id);

        // Revertir el efecto financiero anterior
        revertirMovimiento(existente);


        // Validar categoría
        if (movimiento.getCategoria() == null
                || movimiento.getCategoria().getId() == null) {

            throw new ReglaNegocioException(
                    "La categoría es obligatoria"
            );
        }

        Categoria categoria = categoriaRepository.findById(
                movimiento.getCategoria().getId()
        ).orElseThrow(() ->
                new RecursoNoEncontradoException(
                        "Categoría no encontrada con ID: "
                                + movimiento.getCategoria().getId()
                )
        );


        // Validar método de pago
        MetodoPago metodoPago = null;

        if (movimiento.getMetodoPago() != null
                && movimiento.getMetodoPago().getId() != null) {

            metodoPago = metodoPagoRepository.findById(
                    movimiento.getMetodoPago().getId()
            ).orElseThrow(() ->
                    new RecursoNoEncontradoException(
                            "Método de pago no encontrado con ID: "
                                    + movimiento.getMetodoPago().getId()
                    )
            );
        }


        // Validar monto
        validarMonto(movimiento.getMonto());


        // Validar tipo
        if (movimiento.getTipoMovimiento() == null) {

            throw new ReglaNegocioException(
                    "El tipo de movimiento es obligatorio"
            );
        }


        // Actualizar datos
        existente.setDescripcion(
                movimiento.getDescripcion()
        );

        existente.setFechaMovimiento(
                movimiento.getFechaMovimiento()
        );

        existente.setCategoria(
                categoria
        );

        existente.setMetodoPago(
                metodoPago
        );

        existente.setMonto(
                movimiento.getMonto()
        );

        existente.setTipoMovimiento(
                movimiento.getTipoMovimiento()
        );

        existente.setCuentaOrigen(
                movimiento.getCuentaOrigen()
        );

        existente.setCuentaDestino(
                movimiento.getCuentaDestino()
        );

        existente.setMeta(
                movimiento.getMeta()
        );


        // Aplicar nuevamente el efecto financiero
        aplicarMovimiento(existente);


        return movimientoRepository.save(existente);
    }


    // =========================================================
    // ELIMINAR
    // =========================================================

    @Override
    public void eliminar(Long id) {

        Movimiento movimiento = buscarPorId(id);


        // Buscar comprobante asociado
        Comprobante comprobante =
                comprobanteRepository.findByMovimientoId(id);


        // Eliminar primero el comprobante
        if (comprobante != null) {
            comprobanteRepository.delete(comprobante);
        }


        // Revertir el efecto financiero
        revertirMovimiento(movimiento);


        // Eliminar finalmente el movimiento
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
            String descripcion) {

        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Cuenta no encontrada con ID: " + cuentaId
                        )
                );

        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ReglaNegocioException("Categoría no encontrada"));

        validarCategoriaParaMovimiento(categoria, TipoMovimiento.GASTO);

        MetodoPago metodoPago = metodoPagoRepository.findById(metodoPagoId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Método de pago no encontrado con ID: "
                                        + metodoPagoId
                        )
                );


        validarMonto(monto);

        validarSaldo(cuenta, monto);


        cuenta.setSaldoActual(
                cuenta.getSaldoActual().subtract(monto)
        );


        Movimiento movimiento = new Movimiento();

        movimiento.setCuentaOrigen(cuenta);
        movimiento.setCategoria(categoria);
        movimiento.setMetodoPago(metodoPago);
        movimiento.setMonto(monto);
        movimiento.setDescripcion(descripcion);
        movimiento.setTipoMovimiento(
                TipoMovimiento.GASTO
        );
        movimiento.setFechaMovimiento(
                LocalDateTime.now()
        );


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

        validarCategoriaParaMovimiento(categoria, TipoMovimiento.INGRESO);

        MetodoPago metodoPago = metodoPagoRepository.findById(metodoPagoId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Método de pago no encontrado con ID: "
                                        + metodoPagoId
                        )
                );


        validarMonto(monto);


        cuenta.setSaldoActual(
                cuenta.getSaldoActual().add(monto)
        );


        Movimiento movimiento = new Movimiento();

        movimiento.setCuentaDestino(cuenta);
        movimiento.setCategoria(categoria);
        movimiento.setMetodoPago(metodoPago);
        movimiento.setMonto(monto);
        movimiento.setDescripcion(descripcion);
        movimiento.setTipoMovimiento(
                TipoMovimiento.INGRESO
        );
        movimiento.setFechaMovimiento(
                LocalDateTime.now()
        );


        cuentaRepository.save(cuenta);
        movimientoRepository.save(movimiento);
    }


    // =========================================================
    // TRANSFERENCIA
    // =========================================================

    @Override
    public void transferir(
            Long cuentaOrigenId,
            Long cuentaDestinoId,
            BigDecimal monto,
            String descripcion) {

        Cuenta origen = cuentaRepository.findById(cuentaOrigenId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Cuenta origen no encontrada"
                        )
                );

        Cuenta destino = cuentaRepository.findById(cuentaDestinoId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Cuenta destino no encontrada"
                        )
                );


        validarMonto(monto);


        if (origen.getId().equals(destino.getId())) {

        	throw new ReglaNegocioException(
        	        "La categoría seleccionada no corresponde a un movimiento de INGRESO"
        	);
        }


        validarSaldo(origen, monto);


        origen.setSaldoActual(
                origen.getSaldoActual().subtract(monto)
        );

        destino.setSaldoActual(
                destino.getSaldoActual().add(monto)
        );


        Movimiento movimiento = new Movimiento();

        movimiento.setCuentaOrigen(origen);
        movimiento.setCuentaDestino(destino);
        movimiento.setMonto(monto);
        movimiento.setDescripcion(descripcion);
        movimiento.setTipoMovimiento(
                TipoMovimiento.TRANSFERENCIA
        );
        movimiento.setFechaMovimiento(
                LocalDateTime.now()
        );


        cuentaRepository.save(origen);
        cuentaRepository.save(destino);
        movimientoRepository.save(movimiento);
    }


    // =========================================================
    // APORTE A META
    // =========================================================

    @Override
    public void aportarMeta(
            Long cuentaId,
            Long metaId,
            BigDecimal monto,
            String descripcion) {

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


        validarMonto(monto);

        validarSaldo(cuenta, monto);


        BigDecimal nuevoMonto = meta.getMontoActual()
                .add(monto);


        if (nuevoMonto.compareTo(
                meta.getMontoObjetivo()
        ) > 0) {

            throw new ReglaNegocioException(
                    "El aporte supera el monto objetivo de la meta"
            );
        }


        cuenta.setSaldoActual(
                cuenta.getSaldoActual().subtract(monto)
        );

        meta.setMontoActual(
                nuevoMonto
        );


        Movimiento movimiento = new Movimiento();

        movimiento.setCuentaOrigen(cuenta);
        movimiento.setMeta(meta);
        movimiento.setMonto(monto);
        movimiento.setDescripcion(descripcion);
        movimiento.setTipoMovimiento(
                TipoMovimiento.APORTE_META
        );
        movimiento.setFechaMovimiento(
                LocalDateTime.now()
        );


        cuentaRepository.save(cuenta);
        metaRepository.save(meta);
        movimientoRepository.save(movimiento);
    }


    // =========================================================
    // LISTAR POR TIPO Y USUARIO
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> listarPorTipoYUsuario(
            String tipo,
            Long usuarioId) {

        return movimientoRepository
                .findByTipoMovimientoAndCuentaOrigenUsuarioId(
                        TipoMovimiento.valueOf(
                                tipo.toUpperCase()
                        ),
                        usuarioId
                );
    }


    // =========================================================
    // LISTAR POR CUENTA
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> listarPorCuenta(Long cuentaId) {

        return movimientoRepository
                .findByCuentaOrigenIdOrderByFechaMovimientoDesc(
                        cuentaId
                );
    }


    // =========================================================
    // REVERTIR MOVIMIENTO
    // =========================================================

    private void revertirMovimiento(
            Movimiento movimiento) {

        BigDecimal monto = movimiento.getMonto();


        switch (movimiento.getTipoMovimiento()) {

            case GASTO -> {

                Cuenta cuenta =
                        movimiento.getCuentaOrigen();

                if (cuenta != null) {

                    cuenta.setSaldoActual(
                            cuenta.getSaldoActual().add(monto)
                    );

                    cuentaRepository.save(cuenta);
                }
            }


            case INGRESO -> {

                Cuenta cuenta =
                        movimiento.getCuentaDestino();

                if (cuenta != null) {

                    if (cuenta.getSaldoActual()
                            .compareTo(monto) < 0) {

                        throw new ReglaNegocioException(
                                "No se puede revertir el ingreso "
                                        + "porque el saldo actual "
                                        + "es insuficiente"
                        );
                    }


                    cuenta.setSaldoActual(
                            cuenta.getSaldoActual().subtract(monto)
                    );

                    cuentaRepository.save(cuenta);
                }
            }


            case TRANSFERENCIA -> {

                Cuenta origen =
                        movimiento.getCuentaOrigen();

                Cuenta destino =
                        movimiento.getCuentaDestino();


                if (origen != null) {

                    origen.setSaldoActual(
                            origen.getSaldoActual().add(monto)
                    );

                    cuentaRepository.save(origen);
                }


                if (destino != null) {

                    if (destino.getSaldoActual()
                            .compareTo(monto) < 0) {

                        throw new ReglaNegocioException(
                                "No se puede revertir la transferencia "
                                        + "porque el saldo destino "
                                        + "es insuficiente"
                        );
                    }


                    destino.setSaldoActual(
                            destino.getSaldoActual().subtract(monto)
                    );

                    cuentaRepository.save(destino);
                }
            }


            case APORTE_META -> {

                Cuenta cuenta =
                        movimiento.getCuentaOrigen();

                Meta meta =
                        movimiento.getMeta();


                if (cuenta != null) {

                    cuenta.setSaldoActual(
                            cuenta.getSaldoActual().add(monto)
                    );

                    cuentaRepository.save(cuenta);
                }


                if (meta != null) {

                    BigDecimal nuevoMonto =
                            meta.getMontoActual().subtract(monto);


                    if (nuevoMonto.compareTo(
                            BigDecimal.ZERO
                    ) < 0) {

                        nuevoMonto = BigDecimal.ZERO;
                    }


                    meta.setMontoActual(
                            nuevoMonto
                    );

                    metaRepository.save(meta);
                }
            }


            case RETIRO_META -> {

                Cuenta cuenta =
                        movimiento.getCuentaDestino();

                Meta meta =
                        movimiento.getMeta();


                if (cuenta != null) {

                    if (cuenta.getSaldoActual()
                            .compareTo(monto) < 0) {

                        throw new ReglaNegocioException(
                                "No se puede revertir el retiro "
                                        + "de la meta porque el saldo "
                                        + "es insuficiente"
                        );
                    }


                    cuenta.setSaldoActual(
                            cuenta.getSaldoActual().subtract(monto)
                    );

                    cuentaRepository.save(cuenta);
                }


                if (meta != null) {

                    meta.setMontoActual(
                            meta.getMontoActual().add(monto)
                    );

                    metaRepository.save(meta);
                }
            }
        }
    }


    // =========================================================
    // APLICAR MOVIMIENTO
    // =========================================================

    private void aplicarMovimiento(
            Movimiento movimiento) {

        BigDecimal monto =
                movimiento.getMonto();

        validarMonto(monto);


        switch (movimiento.getTipoMovimiento()) {

            case GASTO -> {

                Cuenta cuenta =
                        obtenerCuenta(
                                movimiento.getCuentaOrigen()
                        );

                validarSaldo(cuenta, monto);


                cuenta.setSaldoActual(
                        cuenta.getSaldoActual().subtract(monto)
                );


                cuentaRepository.save(cuenta);

                movimiento.setCuentaOrigen(cuenta);
            }


            case INGRESO -> {

                Cuenta cuenta =
                        obtenerCuenta(
                                movimiento.getCuentaDestino()
                        );


                cuenta.setSaldoActual(
                        cuenta.getSaldoActual().add(monto)
                );


                cuentaRepository.save(cuenta);

                movimiento.setCuentaDestino(cuenta);
            }


            case TRANSFERENCIA -> {

                Cuenta origen =
                        obtenerCuenta(
                                movimiento.getCuentaOrigen()
                        );

                Cuenta destino =
                        obtenerCuenta(
                                movimiento.getCuentaDestino()
                        );


                if (origen.getId().equals(
                        destino.getId()
                )) {

                    throw new ReglaNegocioException(
                            "La cuenta origen y destino "
                                    + "no pueden ser la misma"
                    );
                }


                validarSaldo(origen, monto);


                origen.setSaldoActual(
                        origen.getSaldoActual().subtract(monto)
                );

                destino.setSaldoActual(
                        destino.getSaldoActual().add(monto)
                );


                cuentaRepository.save(origen);
                cuentaRepository.save(destino);


                movimiento.setCuentaOrigen(origen);
                movimiento.setCuentaDestino(destino);
            }


            case APORTE_META -> {

                Cuenta cuenta =
                        obtenerCuenta(
                                movimiento.getCuentaOrigen()
                        );

                Meta meta =
                        obtenerMeta(
                                movimiento.getMeta()
                        );


                validarSaldo(cuenta, monto);


                BigDecimal nuevoMonto =
                        meta.getMontoActual().add(monto);


                if (nuevoMonto.compareTo(
                        meta.getMontoObjetivo()
                ) > 0) {

                    throw new ReglaNegocioException(
                            "El aporte supera el monto objetivo "
                                    + "de la meta"
                    );
                }


                cuenta.setSaldoActual(
                        cuenta.getSaldoActual().subtract(monto)
                );

                meta.setMontoActual(
                        nuevoMonto
                );


                cuentaRepository.save(cuenta);
                metaRepository.save(meta);


                movimiento.setCuentaOrigen(cuenta);
                movimiento.setMeta(meta);
            }


            case RETIRO_META -> {

                Meta meta =
                        obtenerMeta(
                                movimiento.getMeta()
                        );

                Cuenta cuenta =
                        obtenerCuenta(
                                movimiento.getCuentaDestino()
                        );


                if (meta.getMontoActual()
                        .compareTo(monto) < 0) {

                    throw new ReglaNegocioException(
                            "La meta no tiene saldo suficiente"
                    );
                }


                meta.setMontoActual(
                        meta.getMontoActual().subtract(monto)
                );

                cuenta.setSaldoActual(
                        cuenta.getSaldoActual().add(monto)
                );


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

        if (cuenta == null
                || cuenta.getId() == null) {

            throw new ReglaNegocioException(
                    "La cuenta es obligatoria"
            );
        }


        return cuentaRepository.findById(
                cuenta.getId()
        ).orElseThrow(() ->
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

        if (meta == null
                || meta.getId() == null) {

            throw new ReglaNegocioException(
                    "La meta es obligatoria"
            );
        }


        return metaRepository.findById(
                meta.getId()
        ).orElseThrow(() ->
                new RecursoNoEncontradoException(
                        "Meta no encontrada con ID: "
                                + meta.getId()
                )
        );
    }


    // =========================================================
    // VALIDAR MONTO
    // =========================================================

    private void validarMonto(
            BigDecimal monto) {

        if (monto == null
                || monto.compareTo(
                        BigDecimal.ZERO
                ) <= 0) {

            throw new ReglaNegocioException(
                    "El monto debe ser mayor a cero"
            );
        }
    }
    
    private void validarCategoriaParaMovimiento(Categoria categoria, TipoMovimiento tipoMovimiento) {

        if (categoria == null) {
            if (tipoMovimiento == TipoMovimiento.GASTO ||
                tipoMovimiento == TipoMovimiento.INGRESO) {

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

        if (tipoMovimiento == TipoMovimiento.GASTO &&
                !"GASTO".equalsIgnoreCase(tipoCategoria)) {

            throw new ReglaNegocioException(
                    "La categoría seleccionada no corresponde a un movimiento de GASTO"
            );
        }

        if (tipoMovimiento == TipoMovimiento.INGRESO &&
                !"INGRESO".equalsIgnoreCase(tipoCategoria)) {

            throw new ReglaNegocioException(
                    "La categoría seleccionada no corresponde a un movimiento de INGRESO"
            );
        }
    }
    


    // =========================================================
    // VALIDAR SALDO
    // =========================================================

    private void validarSaldo(
            Cuenta cuenta,
            BigDecimal monto) {

        if (cuenta.getSaldoActual()
                .compareTo(monto) < 0) {

            throw new ReglaNegocioException(
                    "Saldo insuficiente"
            );
        }
    }
}