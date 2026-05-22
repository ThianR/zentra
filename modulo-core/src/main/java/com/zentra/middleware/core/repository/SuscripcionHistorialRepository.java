package com.zentra.middleware.core.repository;

import com.zentra.middleware.core.model.SuscripcionHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuscripcionHistorialRepository extends JpaRepository<SuscripcionHistorial, String> {
}
