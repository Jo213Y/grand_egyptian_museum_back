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

    /** The user this log action was performed ON (nullable — not all actions target a user) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = true)
    private User targetUser;
}