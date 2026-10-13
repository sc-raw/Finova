
package com.finova.service.imp;

import com.finova.entity.Banco;
import com.finova.exception.RecursoNoEncontradoException;
import com.finova.exception.ReglaNegocioException;
import com.finova.repository.BancoRepository;
import com.finova.service.BancoService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BancoServiceImp implements BancoService {

    private final BancoRepository bancoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Banco> listarBancos() {
        return bancoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Banco> listarActivos() {
        return bancoRepository.findByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Banco buscarPorId(Long id) {
        validarId(id);

        return bancoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Banco no encontrado con ID: " + id
                        )
                );
    }

    @Override
    public Banco guardar(Banco banco) {
        validarBanco(banco);

        banco.setNombre(banco.getNombre().trim());

        if (banco.getActivo() == null) {
            banco.setActivo(true);
        }

        validarNombreDuplicado(banco.getNombre(), null);

        return bancoRepository.save(banco);
    }

    @Override
    public Banco actualizar(Long id, Banco actualizado) {
        validarId(id);
        validarBanco(actualizado);

        Banco existente = buscarPorId(id);
        String nombre = actualizado.getNombre().trim();

        validarNombreDuplicado(nombre, id);

        existente.setNombre(nombre);

        if (actualizado.getActivo() != null) {
            existente.setActivo(actualizado.getActivo());
        }

        return bancoRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        Banco banco = buscarPorId(id);
        bancoRepository.delete(banco);
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new ReglaNegocioException(
                    "El ID del banco debe ser válido"
            );
        }
    }

    private void validarBanco(Banco banco) {
        if (banco == null) {
            throw new ReglaNegocioException(
                    "Los datos del banco son obligatorios"
            );
        }

        if (banco.getNombre() == null ||
                banco.getNombre().isBlank()) {
            throw new ReglaNegocioException(
                    "El nombre del banco es obligatorio"
            );
        }
    }

    private void validarNombreDuplicado(String nombre, Long idExcluir) {
        Banco encontrado = bancoRepository.findByNombre(nombre);

        if (encontrado != null &&
                (idExcluir == null ||
                        !encontrado.getId().equals(idExcluir))) {
            throw new ReglaNegocioException(
                    "Ya existe un banco con el nombre: " + nombre
            );
        }
    }
}
