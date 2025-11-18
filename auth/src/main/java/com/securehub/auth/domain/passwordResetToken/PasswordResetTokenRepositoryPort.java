package com.securehub.auth.domain.passwordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepositoryPort {

    PasswordResetToken save(PasswordResetToken passwordResetToken);

    Optional<PasswordResetToken> findByUserIdAndTokenAndConfirmedAtIsNull(String userId, String code);
}
