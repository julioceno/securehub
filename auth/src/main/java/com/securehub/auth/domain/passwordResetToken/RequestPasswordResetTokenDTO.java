package com.securehub.auth.domain.passwordResetToken;

public record RequestPasswordResetTokenDTO(
        String email,
        String token,
        String newPassword
) {
}
