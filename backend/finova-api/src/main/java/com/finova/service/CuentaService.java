package com.finova.service;

import com.finova.entity.Cuenta;
import com.finova.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CuentaService {

    private final CuentaRepository cuentaRepository;

    public List<Cuenta> listarCuentas() {
        return cuentaRepository.findAll();
    }

    public Optional<Cuenta> buscarPorId(Long id) {
        return cuentaRepository.findById(id);
    }

    public Cuenta guardar(Cuenta cuenta) {
        return cuentaRepository.save(cuenta);
    }

    public void eliminar(Long id) {
        cuentaRepository.deleteById(id);
    }
}