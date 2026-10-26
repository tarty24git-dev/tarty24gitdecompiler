package com.cth.tradefinance.profitability.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String refNumber;

    @Column(nullable = false, length = 50)
    private String customerId;

    @Column(nullable = false, length = 50)
    private String productType;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal feeAmount;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal marginPct;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal calculatedProfit;

    @Column(nullable = false, length = 50)
    private String status;

    private LocalDateTime processedDate;

    @PrePersist
    public void onPrePersist() {
        if (processedDate == null) {
            processedDate = LocalDateTime.now();
        }
    }
}
