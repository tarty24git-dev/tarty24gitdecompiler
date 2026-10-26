package com.cth.tradefinance.report.controller;

import com.cth.tradefinance.report.service.DynamicReportService;
import com.cth.tradefinance.report.service.ReportSchemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ReportController {

    private final ReportSchemaService schemaService;
    private final DynamicReportService dynamicReportService;

    @GetMapping("/reports")
    public String reportsPage(Model model) {
        model.addAttribute("schemaMap", schemaService.getAvailableTablesAndFields());
        return "reports";
    }

    @GetMapping("/api/reports/schema")
    @ResponseBody
    public ResponseEntity<?> getSchema() {
        return ResponseEntity.ok(schemaService.getAvailableTablesAndFields());
    }

    @PostMapping("/api/reports/generate")
    @ResponseBody
    public ResponseEntity<?> generateReport(@RequestBody ReportRequest request) {
        Map<String, Object> result = dynamicReportService.executeDynamicReportQuery(
                request.getEntityName(),
                request.getFields(),
                request.getFilterField(),
                request.getFilterValue()
        );
        return ResponseEntity.ok(result);
    }

    @PostMapping("/api/reports/export/csv")
    public ResponseEntity<byte[]> exportCsv(@RequestBody ReportRequest request) {
        Map<String, Object> result = dynamicReportService.executeDynamicReportQuery(
                request.getEntityName(),
                request.getFields(),
                request.getFilterField(),
                request.getFilterValue()
        );

        @SuppressWarnings("unchecked")
        List<String> headers = (List<String>) result.get("headers");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> data = (List<Map<String, Object>>) result.get("data");

        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", headers)).append("\n");

        for (Map<String, Object> row : data) {
            for (int i = 0; i < headers.size(); i++) {
                Object val = row.get(headers.get(i));
                sb.append(val != null ? "\"" + val.toString().replace("\"", "\"\"") + "\"" : "");
                if (i < headers.size() - 1) sb.append(",");
            }
            sb.append("\n");
        }

        byte[] csvBytes = sb.toString().getBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + request.getEntityName() + "_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }

    @lombok.Data
    public static class ReportRequest {
        private String entityName;
        private List<String> fields;
        private String filterField;
        private String filterValue;
    }
}
