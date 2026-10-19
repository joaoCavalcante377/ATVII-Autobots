package com.autobots.automanager.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@Data
@org.springframework.hateoas.server.core.Relation(itemRelation = "endereco", collectionRelation = "enderecos")
@Entity
public class Endereco {
	@Id()
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = true)
	private String estado;
	@Column(nullable = false)
	@NotBlank
	private String cidade;
	@Column(nullable = true)
	private String bairro;
	@Column(nullable = false)
	@NotBlank
	private String rua;
	@Column(nullable = false)
	@NotBlank
	private String numero;
	@Column(nullable = true)
	private String codigoPostal;
	@Column(unique = false, nullable = true)
	private String informacoesAdicionais;

}