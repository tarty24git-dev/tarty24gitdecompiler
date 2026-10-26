package com.cth.tradefinance.decompiler.service;

import com.cth.tradefinance.decompiler.entity.DecompileHistory;
import com.cth.tradefinance.decompiler.repository.DecompileHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benf.cfr.reader.Main;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class JarDecompilerService {

    private final DecompileHistoryRepository historyRepository;

    @Value("${cth.app.decompiler.output-dir:./decompiled_sources}")
    private String defaultOutputDir;

    public DecompileHistory decompileJar(MultipartFile file, String customOutputDir, String username) throws Exception {
        String jarName = file.getOriginalFilename();
        if (jarName == null || jarName.isEmpty()) {
            jarName = "uploaded_" + System.currentTimeMillis() + ".jar";
        }

        String targetDir = (customOutputDir != null && !customOutputDir.trim().isEmpty())
                ? customOutputDir.trim()
                : defaultOutputDir + "/" + jarName.replaceAll("[^a-zA-Z0-9.-]", "_") + "_" + System.currentTimeMillis();

        File targetFolder = new File(targetDir);
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }

        File tempJar = File.createTempFile("upload_", ".jar");
        file.transferTo(tempJar);

        AtomicInteger classCount = new AtomicInteger(0);

        try {
            String[] cfrArgs = new String[] {
                    tempJar.getAbsolutePath(),
                    "--outputdir",
                    targetFolder.getAbsolutePath(),
                    "--showversion",
                    "false"
            };

            Main.main(cfrArgs);

            try (JarFile jar = new JarFile(tempJar)) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().endsWith(".class")) {
                        classCount.incrementAndGet();
                    }
                }
            }

            DecompileHistory history = DecompileHistory.builder()
                    .jarName(jarName)
                    .outputDirectory(targetFolder.getAbsolutePath())
                    .decompiledClassCount(classCount.get())
                    .status("SUCCESS")
                    .processedAt(LocalDateTime.now())
                    .processedBy(username != null ? username : "System")
                    .build();

            return historyRepository.save(history);
        } catch (Exception e) {
            log.error("Failed to decompile JAR: {}", jarName, e);
            DecompileHistory history = DecompileHistory.builder()
                    .jarName(jarName)
                    .outputDirectory(targetFolder.getAbsolutePath())
                    .decompiledClassCount(0)
                    .status("FAILED: " + e.getMessage())
                    .processedAt(LocalDateTime.now())
                    .processedBy(username != null ? username : "System")
                    .build();
            historyRepository.save(history);
            throw e;
        } finally {
            if (tempJar.exists()) {
                tempJar.delete();
            }
        }
    }

    public List<DecompileHistory> getHistory() {
        return historyRepository.findAllByOrderByProcessedAtDesc();
    }

    public List<Map<String, String>> listDecompiledFiles(Long historyId) throws Exception {
        DecompileHistory history = historyRepository.findById(historyId)
                .orElseThrow(() -> new IllegalArgumentException("History record not found: " + historyId));

        File folder = new File(history.getOutputDirectory());
        List<Map<String, String>> fileList = new ArrayList<>();

        if (folder.exists() && folder.isDirectory()) {
            try (var stream = Files.walk(folder.toPath())) {
                stream.filter(Files::isRegularFile)
                        .forEach(path -> {
                            Map<String, String> item = new HashMap<>();
                            item.put("relativePath", folder.toPath().relativize(path).toString());
                            item.put("fullPath", path.toAbsolutePath().toString());
                            fileList.add(item);
                        });
            }
        }
        return fileList;
    }

    public String getFileContent(String filePath) throws Exception {
        Path path = Paths.get(filePath).normalize().toAbsolutePath();

        Path allowedRoot = Paths.get(defaultOutputDir).normalize().toAbsolutePath();
        if (!path.startsWith(allowedRoot) && !path.toString().contains("decompiled_sources")) {
            throw new SecurityException("Access denied: Requested path is outside the allowed decompilation directory.");
        }

        if (Files.exists(path)) {
            return Files.readString(path);
        }
        return "// File not found: " + filePath;
    }
}
