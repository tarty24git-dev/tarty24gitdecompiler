package com.cth.tradefinance.profitability.repository;

import com.cth.tradefinance.profitability.entity.TradeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TradeTransactionRepository extends JpaRepository<TradeTransaction, Long> {
    Optional<TradeTransaction> findByRefNumber(String refNumber);
}
