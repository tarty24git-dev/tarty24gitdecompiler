package com.cth.tradefinance.dashboard.controller;

import com.cth.tradefinance.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Value("${cth.app.title:CTH Trade Finance System}")
    private String appTitle;

    @GetMapping("/")
    public String indexPage(Model model) {
        model.addAttribute("appTitle", appTitle);
        model.addAttribute("metrics", dashboardService.getDashboardMetrics());
        return "index";
    }

    @GetMapping("/api/dashboard/metrics")
    @ResponseBody
    public ResponseEntity<?> getMetrics() {
        return ResponseEntity.ok(dashboardService.getDashboardMetrics());
    }
}
