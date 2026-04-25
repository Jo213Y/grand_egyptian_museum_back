package com.gem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Maps EXACTLY to: users
 * user_id | full_name | email | password | nationality | user_type |
 * ssn | passport_number | phone_number | role_id | created_at
 */
@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotBlank
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Email @NotBlank
    @Column(name = "email", unique = true, nullable = false, length = 150)
    private String email;

    @NotBlank
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "nationality", length = 50)
    private String nationality;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserType userType;

    @Column(name = "ssn", unique = true, length = 20)
    private String ssn;

    @Column(name = "passport_number", unique = true, length = 30)
    private String passportNumber;

    @Column(name = "phone_number", length = 20)
    private String phone;

    /**
     * role_id FK → roles table (1=USER, 2=ADMIN, 3=BLOCK)
     * We keep a simple integer that Hibernate won't try to join,
     * and derive the Role enum from it via lifecycle hooks.
     */
    @Column(name = "role_id")
    @Builder.Default
    private Integer roleId = 1;

    @Transient
    @Builder.Default
    private Role role = Role.USER;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // ── enums ────────────────────────────────────────────────────────────────

    public enum Role    { USER, ADMIN, BLOCK }
    public enum UserType { EGYPTIAN, FOREIGN }

    // ── sync role enum ↔ roleId ──────────────────────────────────────────────

    @PostLoad
    public void syncRoleFromId() {
        if (roleId != null) {
            role = switch (roleId) {
                case 2  -> Role.ADMIN;
                case 3  -> Role.BLOCK;
                default -> Role.USER;
            };
        }
    }

    @PrePersist
    @PreUpdate
    public void syncIdFromRole() {
        if (role == null) role = Role.USER;
        roleId = switch (role) {
            case ADMIN -> 2;
            case BLOCK -> 3;
            default    -> 1;
        };
    }

    /** Convenience getter used in security layer */
    public Role getRole() {
        syncRoleFromId();
        return role != null ? role : Role.USER;
    }
}
