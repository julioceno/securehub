package com.securehub.auth.domain.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

// TODO: seria interessante esse DTO ficar dentro do inbound

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