package com.securehub.auth.adapters.in.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ForgotPasswordDTO(
        @NotBlank(message = "{validation.required}")
        @Email(message = "{validation.invalid}")
        String email
) {
}
