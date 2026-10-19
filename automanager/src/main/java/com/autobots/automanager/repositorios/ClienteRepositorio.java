package com.autobots.automanager.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.automanager.entidades.Cliente;

public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {
 java.util.Optional<Cliente> findByDocumentosId(Long id);
 java.util.Optional<Cliente> findByTelefonesId(Long id);
 java.util.Optional<Cliente> findByEnderecoId(Long id);
}