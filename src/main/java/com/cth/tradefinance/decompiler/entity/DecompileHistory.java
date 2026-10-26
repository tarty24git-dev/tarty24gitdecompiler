package com.cth.tradefinance.decompiler.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "decompile_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DecompileHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String jarName;

    @Column(nullable = false, length = 500)
    private String outputDirectory;

    private int decompiledClassCount;

    @Column(nullable = false, length = 50)
    private String status;

    private LocalDateTime processedAt;

    private String processedBy;

    @PrePersist
    public void onPrePersist() {
        if (processedAt == null) {
            processedAt = LocalDateTime.now();
        }
    }
}
