package com.finova.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.finova.entity.Comprobante;
import com.finova.repository.ComprobanteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComprobanteService {
	
	private final ComprobanteRepository comprobanteRepository;

	public List<Comprobante> listarComprobantes() {
		return comprobanteRepository.findAll();
	}
	
	public Optional<Comprobante> buscarPorId (Long id) {
		return comprobanteRepository.findById(id);
	}
	
	public Comprobante guardar(Comprobante comprobante) {
        return comprobanteRepository.save(comprobante);
    }
	
	public void eliminar ( Long id) {
		comprobanteRepository.deleteById(id);
	}
	
}
