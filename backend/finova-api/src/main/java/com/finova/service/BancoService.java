package com.finova.service;

import com.finova.entity.Banco;
import com.finova.repository.BancoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BancoService {

    private final BancoRepository bancoRepository;

    public List<Banco> listarBancos() {
        return bancoRepository.findAll();
    }

    public Optional<Banco> buscarPorId(Long id) {
        return bancoRepository.findById(id);
    }

    public Banco guardar(Banco banco) {
        return bancoRepository.save(banco);
    }

    public void eliminar(Long id) {
        bancoRepository.deleteById(id);
    }
}