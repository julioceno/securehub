package com.securehub.auth.adapters.in.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserToCreateDTO {
    @NotBlank(message = "{validation.required}")
    private String username;

    @Email(message = "{validation.invalid}")
    @NotBlank(message = "{validation.required}")
    private String email;

    @NotBlank(message = "{validation.required}")
    @Size(min = 6, max = 20, message = "{validation.size}")
    private String password;

}