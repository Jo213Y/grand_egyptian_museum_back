package com.gem.dto;

import jakarta.validation.constraints.*;
import lombok.*;

// ── Auth ─────────────────────────────────────────────────────
@Data
public class LoginRequest {
    @Email @NotBlank
    private String email;
    @NotBlank
    private String password;
}
