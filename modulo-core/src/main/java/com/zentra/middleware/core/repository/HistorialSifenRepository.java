package com.zentra.middleware.core.repository;

import com.zentra.middleware.core.model.HistorialSifen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialSifenRepository extends JpaRepository<HistorialSifen, String> {
    List<HistorialSifen> findByDocumentoIdOrderByFechaRegistroDesc(String documentoId);
}
