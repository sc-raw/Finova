package com.finova.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.finova.entity.Meta;
import com.finova.repository.MetaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetaService {
	
	private final MetaRepository metaRepository;
	
	public List<Meta> listarMetas() {
		return metaRepository.findAll();
	}
	
	public Optional<Meta> buscarPorId(Long id) {
		return metaRepository.findById(id);
	}
	
	public Meta guardar ( Meta meta) {
		return metaRepository.save(meta);
	}
	
	public void eliminar (Long id) {
		metaRepository.deleteById(id);
	}
}
