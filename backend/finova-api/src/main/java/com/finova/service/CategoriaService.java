package com.finova.service;

import org.springframework.stereotype.Service;

import com.finova.entity.Categoria;
import com.finova.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CategoriaService {
	
	private final CategoriaRepository categoriaRepository;
	
	public List<Categoria> listarCategorias() {
		return categoriaRepository.findAll();
	}
	
	public Optional<Categoria> buscarPorId(Long id) {
		return categoriaRepository.findById(id);
	}
	
	public Categoria guardar (Categoria categoria) {
		return categoriaRepository.save(categoria);
	}
	
	public void eliminar (Long id) {
		categoriaRepository.deleteById(id);
	}
	
	
}
