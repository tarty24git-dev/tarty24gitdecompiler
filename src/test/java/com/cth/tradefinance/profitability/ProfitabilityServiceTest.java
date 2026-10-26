package com.cth.tradefinance.profitability;

import com.cth.tradefinance.profitability.service.CsvProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("h2")
public class ProfitabilityServiceTest {

    @Autowired
    private CsvProcessingService csvProcessingService;

    @Test
    void testProcessTradeCsvs() throws Exception {
        String inputCsvContent = "refNumber,customerId,productType,currency,amount,feeAmount,marginPct\n" +
                "TEST001,CUST01,Letter of Credit,USD,100000.00,500.00,2.00\n";

        String responseCsvContent = "refNumber,status,remarks\n" +
                "TEST001,PROCESSED_SUCCESS,Approved\n";

        MockMultipartFile inputFile = new MockMultipartFile("inputFile", "input.csv", "text/csv", inputCsvContent.getBytes());
        MockMultipartFile responseFile = new MockMultipartFile("responseFile", "response.csv", "text/csv", responseCsvContent.getBytes());

        Map<String, Object> result = csvProcessingService.processTradeCsvs(inputFile, responseFile);

        assertEquals(1, result.get("totalRecords"));
        assertEquals(1, result.get("successCount"));
        assertEquals(0, result.get("rejectCount"));

        BigDecimal totalProfit = (BigDecimal) result.get("totalProfitability");
        assertEquals(new BigDecimal("2500.00"), totalProfit);
    }
}
