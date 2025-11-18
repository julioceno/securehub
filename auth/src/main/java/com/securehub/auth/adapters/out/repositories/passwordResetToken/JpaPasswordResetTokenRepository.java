package com.securehub.auth.adapters.out.repositories.passwordResetToken;

import com.securehub.auth.adapters.out.entities.JpaPasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaPasswordResetTokenRepository extends JpaRepository<JpaPasswordResetTokenEntity, String> {
    Optional<JpaPasswordResetTokenEntity> findByUserIdAndTokenAndConfirmedAtIsNull(String userId, String token);
}
