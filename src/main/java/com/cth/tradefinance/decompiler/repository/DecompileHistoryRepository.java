package com.cth.tradefinance.decompiler.repository;

import com.cth.tradefinance.decompiler.entity.DecompileHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DecompileHistoryRepository extends JpaRepository<DecompileHistory, Long> {
    List<DecompileHistory> findAllByOrderByProcessedAtDesc();
}
