package com.cth.tradefinance.profitability.controller;

import com.cth.tradefinance.profitability.service.CsvProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ProfitabilityController {

    private final CsvProcessingService csvProcessingService;

    @GetMapping("/profitability")
    public String profitabilityPage(Model model) {
        model.addAttribute("transactions", csvProcessingService.getAllTransactions());
        return "profitability";
    }

    @PostMapping("/api/profitability/process")
    @ResponseBody
    public ResponseEntity<?> processCsvs(
            @RequestParam("inputFile") MultipartFile inputFile,
            @RequestParam(value = "responseFile", required = false) MultipartFile responseFile) {
        try {
            if (inputFile.isEmpty()) {
                Map<String, String> err = new HashMap<>();
                err.put("error", "Input CSV file is required");
                return ResponseEntity.badRequest().body(err);
            }
            Map<String, Object> result = csvProcessingService.processTradeCsvs(inputFile, responseFile);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Error processing CSV files: " + e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @GetMapping("/api/profitability/transactions")
    @ResponseBody
    public ResponseEntity<?> getTransactions() {
        return ResponseEntity.ok(csvProcessingService.getAllTransactions());
    }
}
