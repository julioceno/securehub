package com.securehub.auth.adapters.in.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EnableUserDTO (
    @NotBlank(message = "{validation.required}")
    @Email(message = "{validation.invalid}")
    String email,

    @NotBlank(message = "{validation.required}")
    String code
) {
}
