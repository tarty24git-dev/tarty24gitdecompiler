package com.cth.tradefinance.decompiler.controller;

import com.cth.tradefinance.decompiler.entity.DecompileHistory;
import com.cth.tradefinance.decompiler.service.JarDecompilerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DecompilerController {

    private final JarDecompilerService decompilerService;

    @GetMapping("/decompiler")
    public String decompilerPage(Model model) {
        model.addAttribute("historyList", decompilerService.getHistory());
        return "decompiler";
    }

    @PostMapping("/api/decompile/upload")
    @ResponseBody
    public ResponseEntity<?> uploadAndDecompile(
            @RequestParam("jarFile") MultipartFile jarFile,
            @RequestParam(value = "outputDirectory", required = false) String outputDirectory,
            Principal principal) {
        try {
            if (jarFile.isEmpty()) {
                Map<String, String> err = new HashMap<>();
                err.put("error", "Please select a JAR file to decompile.");
                return ResponseEntity.badRequest().body(err);
            }
            String username = principal != null ? principal.getName() : "admin";
            DecompileHistory history = decompilerService.decompileJar(jarFile, outputDirectory, username);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Decompilation failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @GetMapping("/api/decompile/history")
    @ResponseBody
    public ResponseEntity<?> getHistory() {
        return ResponseEntity.ok(decompilerService.getHistory());
    }

    @GetMapping("/api/decompile/history/{id}/files")
    @ResponseBody
    public ResponseEntity<?> getDecompiledFiles(@PathVariable Long id) {
        try {
            List<Map<String, String>> files = decompilerService.listDecompiledFiles(id);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @GetMapping("/api/decompile/file")
    @ResponseBody
    public ResponseEntity<?> getFileContent(@RequestParam("path") String path) {
        try {
            String content = decompilerService.getFileContent(path);
            Map<String, String> res = new HashMap<>();
            res.put("content", content);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }
}
