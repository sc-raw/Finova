package com.finova.service;

import com.finova.entity.Banco;
import com.finova.exception.RecursoNoEncontradoException;
import com.finova.exception.ReglaNegocioException;
import com.finova.repository.BancoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BancoService {

    private final BancoRepository bancoRepository;

    public List<Banco> listarBancos() {
        return bancoRepository.findAll();
    }

    public List<Banco> listarActivos() {
        return bancoRepository.findByActivoTrue();
    }

    public Banco buscarPorId(Long id) {
        return bancoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Banco no encontrado con ID: " + id));
    }

    @Transactional
    public Banco guardar(Banco banco) {
        if (banco.getNombre() == null || banco.getNombre().isBlank()) {
            throw new ReglaNegocioException("El nombre del banco es obligatorio");
        }
        if (banco.getActivo() == null) {
            banco.setActivo(true);
        }
        return bancoRepository.save(banco);
    }

    @Transactional
    public Banco actualizar(Long id, Banco actualizado) {
        Banco existente = buscarPorId(id);
        existente.setNombre(actualizado.getNombre());
        existente.setActivo(actualizado.getActivo());
        return bancoRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!bancoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Banco no encontrado con ID: " + id);
        }
        bancoRepository.deleteById(id);
    }
}