package com.finova.repository;

import com.finova.entity.Banco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BancoRepository extends JpaRepository<Banco, Long> {

    // Listar bancos activos
    List<Banco> findByActivoTrue();

    // Buscar por nombre
    Banco findByNombre(String nombre);
}