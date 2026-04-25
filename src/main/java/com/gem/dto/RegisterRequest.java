package com.gem.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String fullName;

    @Email @NotBlank
    private String email;

    @NotBlank @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String phone;

    // Optional fields (aligned with users table)
    private String nationality;
    private String userType;     // "EGYPTIAN" or "FOREIGN"
    private String ssn;
    private String passportNumber;
}
