package com.finova.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "meta")
@Getter
@Setter
@NoArgsConstructor

public class Meta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column( name = "meta_id")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;
	
	@Column(nullable = false)
	private String nombre;
	
	@Column(name="monto_objetivo", nullable = false, precision = 15, scale =2)
	private BigDecimal montoObjetivo;
	
	@Column(name="monto_actual", nullable = false, precision = 15, scale = 2)
	private BigDecimal montoActual;
	
	@Column(name ="fecha_objetivo")
	private LocalDate fechaObjetivo;
}
