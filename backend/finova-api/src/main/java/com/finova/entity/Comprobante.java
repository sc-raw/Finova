package com.finova.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="comprobantes")
@Getter
@Setter
@NoArgsConstructor

public class Comprobante {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comprobantes_id")
	private Long id;
	
	@OneToOne
	@JoinColumn(name = "movimiento_id", nullable = false, unique = true)
	private Movimiento movimiento;
	
	@Column(name = "nombre_comercio")
	private String nombreComercio;
	
	private String ruc;
	
	@Column(name = "codigo_operacion")
	private String codigoOperacion;
	
	@Column(name = "ruta_comprobante")
	private String rutaComprobante;


}


