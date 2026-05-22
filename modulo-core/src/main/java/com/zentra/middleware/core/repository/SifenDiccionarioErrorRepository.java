package com.zentra.middleware.core.repository;

import com.zentra.middleware.core.model.SifenDiccionarioError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SifenDiccionarioErrorRepository extends JpaRepository<SifenDiccionarioError, String> {
    List<SifenDiccionarioError> findByActivoTrue();
}
