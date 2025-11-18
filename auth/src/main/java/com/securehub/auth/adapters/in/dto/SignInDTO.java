package com.securehub.auth.adapters.in.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignInDTO(
        @NotBlank(message = "{validation.required}")
        @Email(message = "{validation.invalid}")
        String email,

        @NotBlank(message = "{validation.required}")
        @Size(min = 6, max = 20, message = "{validation.size}")
        String password
) {
}
