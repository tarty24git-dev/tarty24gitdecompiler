package com.cth.tradefinance.decompiler;

import com.cth.tradefinance.decompiler.entity.DecompileHistory;
import com.cth.tradefinance.decompiler.service.JarDecompilerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("h2")
public class JarDecompilerServiceTest {

    @Autowired
    private JarDecompilerService decompilerService;

    @Test
    void testDecompileJarUpload() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry entry = new ZipEntry("TestDummy.class");
            zos.putNextEntry(entry);
            zos.write(new byte[]{0x01, 0x02, 0x03});
            zos.closeEntry();
        }

        MockMultipartFile jarFile = new MockMultipartFile("jarFile", "sample.jar", "application/java-archive", baos.toByteArray());

        DecompileHistory history = decompilerService.decompileJar(jarFile, null, "admin");

        assertNotNull(history.getId());
        assertEquals("sample.jar", history.getJarName());
        assertEquals("SUCCESS", history.getStatus());
        assertTrue(history.getDecompiledClassCount() >= 0);
    }
}
