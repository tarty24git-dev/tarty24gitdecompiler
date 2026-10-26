package com.cth.tradefinance.profitability.service;

import com.cth.tradefinance.profitability.entity.TradeTransaction;
import com.cth.tradefinance.profitability.repository.TradeTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CsvProcessingService {

    private final TradeTransactionRepository transactionRepository;

    @Transactional
    public Map<String, Object> processTradeCsvs(MultipartFile inputFile, MultipartFile responseFile) throws Exception {
        Map<String, String> responseMap = new HashMap<>();

        if (responseFile != null && !responseFile.isEmpty()) {
            try (Reader reader = new InputStreamReader(responseFile.getInputStream());
                 CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build().parse(reader)) {
                for (CSVRecord record : parser) {
                    String refNumber = record.get("refNumber");
                    String status = record.get("status");
                    if (refNumber != null && status != null) {
                        responseMap.put(refNumber.trim(), status.trim());
                    }
                }
            }
        }

        List<TradeTransaction> processedTransactions = new ArrayList<>();
        int successCount = 0;
        int rejectCount = 0;
        BigDecimal totalProfit = BigDecimal.ZERO;

        try (Reader reader = new InputStreamReader(inputFile.getInputStream());
             CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build().parse(reader)) {

            for (CSVRecord record : parser) {
                String refNumber = record.get("refNumber").trim();
                String customerId = record.get("customerId").trim();
                String productType = record.get("productType").trim();
                String currency = record.get("currency").trim();
                BigDecimal amount = new BigDecimal(record.get("amount").trim());
                BigDecimal feeAmount = new BigDecimal(record.get("feeAmount").trim());
                BigDecimal marginPct = new BigDecimal(record.get("marginPct").trim());

                BigDecimal marginIncome = amount.multiply(marginPct).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                BigDecimal calculatedProfit = marginIncome.add(feeAmount);

                String status = responseMap.getOrDefault(refNumber, "PROCESSED_SUCCESS");

                TradeTransaction tx = TradeTransaction.builder()
                        .refNumber(refNumber)
                        .customerId(customerId)
                        .productType(productType)
                        .currency(currency)
                        .amount(amount)
                        .feeAmount(feeAmount)
                        .marginPct(marginPct)
                        .calculatedProfit(calculatedProfit)
                        .status(status)
                        .processedDate(LocalDateTime.now())
                        .build();

                Optional<TradeTransaction> existing = transactionRepository.findByRefNumber(refNumber);
                if (existing.isPresent()) {
                    TradeTransaction updated = existing.get();
                    updated.setAmount(amount);
                    updated.setFeeAmount(feeAmount);
                    updated.setMarginPct(marginPct);
                    updated.setCalculatedProfit(calculatedProfit);
                    updated.setStatus(status);
                    updated.setProcessedDate(LocalDateTime.now());
                    transactionRepository.save(updated);
                    processedTransactions.add(updated);
                } else {
                    transactionRepository.save(tx);
                    processedTransactions.add(tx);
                }

                if ("REJECTED".equalsIgnoreCase(status) || "FAILED".equalsIgnoreCase(status)) {
                    rejectCount++;
                } else {
                    successCount++;
                    totalProfit = totalProfit.add(calculatedProfit);
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalRecords", processedTransactions.size());
        result.put("successCount", successCount);
        result.put("rejectCount", rejectCount);
        result.put("totalProfitability", totalProfit);
        result.put("transactions", processedTransactions);

        return result;
    }

    public List<TradeTransaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
