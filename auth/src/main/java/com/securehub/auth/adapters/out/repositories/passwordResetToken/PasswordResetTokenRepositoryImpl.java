package com.securehub.auth.adapters.out.repositories.passwordResetToken;

import com.securehub.auth.adapters.out.entities.JpaPasswordResetTokenEntity;
import com.securehub.auth.domain.passwordResetToken.PasswordResetToken;
import com.securehub.auth.domain.passwordResetToken.PasswordResetTokenRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PasswordResetTokenRepositoryImpl implements PasswordResetTokenRepositoryPort {

    private final JpaPasswordResetTokenRepository jpaPasswordResetTokenRepository;

    public PasswordResetTokenRepositoryImpl(JpaPasswordResetTokenRepository jpaPasswordResetTokenRepository) {
        this.jpaPasswordResetTokenRepository = jpaPasswordResetTokenRepository;
    }

    @Override
    public PasswordResetToken save(PasswordResetToken passwordResetToken) {
        JpaPasswordResetTokenEntity entity = new JpaPasswordResetTokenEntity(passwordResetToken);
        JpaPasswordResetTokenEntity passwordResetTokenCreated = jpaPasswordResetTokenRepository.save(entity);
        return toDomain(passwordResetTokenCreated);
    }

    @Override
    public Optional<PasswordResetToken> findByUserIdAndTokenAndConfirmedAtIsNull(String userId, String token) {
        return jpaPasswordResetTokenRepository.findByUserIdAndTokenAndConfirmedAtIsNull(userId, token)
                .map(this::toDomain);
    }

    private PasswordResetToken toDomain(JpaPasswordResetTokenEntity jpaPasswordResetTokenEntity) {
        return new PasswordResetToken(
            jpaPasswordResetTokenEntity.getId(),
            jpaPasswordResetTokenEntity.getUserId(),
            jpaPasswordResetTokenEntity.getToken(),
            jpaPasswordResetTokenEntity.getExpiresAt(),
            jpaPasswordResetTokenEntity.getConfirmedAt()
        );
    }
}
