package com.finova.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.finova.entity.MetodoPago;
import com.finova.repository.MetodoPagoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetodoPagoService {

	private final MetodoPagoRepository metodoPagoRepository;
	
	public List<MetodoPago> listarMetodosPago() {
		return metodoPagoRepository.findAll();
	}
	
	public Optional<MetodoPago> buscarPorId(Long id){
		return metodoPagoRepository.findById(id);
	}
	
	public MetodoPago guardar (MetodoPago metodoPago) {
		return metodoPagoRepository.save(metodoPago);
	}
	
	public void eliminar (Long id) {
		metodoPagoRepository.deleteById(id);
	}
}
