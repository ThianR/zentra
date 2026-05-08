package com.zentra.middleware.core.repository;

import com.zentra.middleware.core.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    Optional<Usuario> findByUsername(String username);
    List<Usuario> findByClienteId(String clienteId);
    boolean existsByUsername(String username);
}
