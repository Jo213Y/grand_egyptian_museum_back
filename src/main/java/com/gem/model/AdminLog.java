package com.gem.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admin_email", length = 150)
    private String adminEmail;

    @Column(name = "admin_name", length = 100)
    private String adminName;

    @Column(name = "action", length = 100)
    private String action;

    @Column(name = "detail", columnDefinition = "TEXT")
    private String detail;

    @Column(name = "timestamp")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}