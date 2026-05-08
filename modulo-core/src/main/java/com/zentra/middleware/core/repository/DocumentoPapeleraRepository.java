package com.zentra.middleware.core.repository;

import com.zentra.middleware.core.model.DocumentoPapelera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoPapeleraRepository extends JpaRepository<DocumentoPapelera, String> {
}
