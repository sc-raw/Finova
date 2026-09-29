package com.finova.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.finova.entity.Movimiento;
import com.finova.repository.MovimientoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovimientoService {
	
	private final MovimientoRepository movimientoRepository;
	
	public List<Movimiento>listarMovimientos() {
		return movimientoRepository.findAll();
	}
	
	public Optional<Movimiento> buscarPorId(Long id){
		return movimientoRepository.findById(id);
	}
	
	public Movimiento guardar ( Movimiento movimiento) {
		return movimientoRepository.save(movimiento);
	}
	
	public void eliminar (Long id) {
		movimientoRepository.deleteById(id);
	}
	
}
