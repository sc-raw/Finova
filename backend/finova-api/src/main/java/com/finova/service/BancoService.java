package com.finova.service;

import com.finova.entity.Banco;

import java.util.List;

public interface BancoService {

    List<Banco> listarBancos();

    List<Banco> listarActivos();

    Banco buscarPorId(Long id);

    Banco guardar(Banco banco);

    Banco actualizar(Long id, Banco bancoActualizado);

    void eliminar(Long id);
}