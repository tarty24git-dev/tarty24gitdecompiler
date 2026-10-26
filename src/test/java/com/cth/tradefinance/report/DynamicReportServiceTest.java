package com.cth.tradefinance.report;

import com.cth.tradefinance.report.service.DynamicReportService;
import com.cth.tradefinance.report.service.ReportSchemaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("h2")
public class DynamicReportServiceTest {

    @Autowired
    private ReportSchemaService schemaService;

    @Autowired
    private DynamicReportService dynamicReportService;

    @Test
    void testSchemaDiscovery() {
        Map<String, List<String>> schema = schemaService.getAvailableTablesAndFields();
        assertNotNull(schema);
        assertTrue(schema.containsKey("User"));
        assertTrue(schema.get("User").contains("username"));
    }

    @Test
    void testExecuteDynamicReportQuery() {
        Map<String, Object> result = dynamicReportService.executeDynamicReportQuery("User", List.of("username", "role"), "username", "admin");
        assertNotNull(result);
        assertTrue(result.containsKey("headers"));
        assertTrue(result.containsKey("data"));
    }
}
