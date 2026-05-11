package com.zentra.middleware.core.repository;

import com.zentra.middleware.core.model.PadronRuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PadronRucRepository extends JpaRepository<PadronRuc, String> {
}
