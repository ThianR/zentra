package com.zentra.middleware.core.repository;

import com.zentra.middleware.core.model.UsuarioInvitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioInvitacionRepository extends JpaRepository<UsuarioInvitacion, String> {

    Optional<UsuarioInvitacion> findByIdAndUsadoFalse(String id);
}
