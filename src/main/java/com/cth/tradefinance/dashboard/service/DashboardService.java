package com.cth.tradefinance.dashboard.service;

import com.cth.tradefinance.decompiler.repository.DecompileHistoryRepository;
import com.cth.tradefinance.profitability.repository.TradeTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardService {

    private final TradeTransactionRepository transactionRepository;
    private final DecompileHistoryRepository decompileRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public Map<String, Object> getDashboardMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        long totalTx = transactionRepository.count();
        long totalDecompiles = decompileRepository.count();

        BigDecimal totalProfit = transactionRepository.findAll().stream()
                .map(tx -> tx.getCalculatedProfit() != null ? tx.getCalculatedProfit() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long heapUsed = memoryBean.getHeapMemoryUsage().getUsed() / (1024 * 1024);
        long heapMax = memoryBean.getHeapMemoryUsage().getMax() / (1024 * 1024);

        metrics.put("totalTransactions", totalTx);
        metrics.put("totalDecompiles", totalDecompiles);
        metrics.put("totalProfitability", totalProfit);
        metrics.put("memoryUsedMb", heapUsed);
        metrics.put("memoryMaxMb", heapMax);
        metrics.put("systemStatus", "OPERATIONAL");

        return metrics;
    }

    @Scheduled(fixedRate = 5000)
    public void pushRealtimeDashboardUpdates() {
        try {
            Map<String, Object> metrics = getDashboardMetrics();
            messagingTemplate.convertAndSend("/topic/dashboard", metrics);
        } catch (Exception e) {
            log.trace("Error broadcasting WebSocket metrics: {}", e.getMessage());
        }
    }
}
