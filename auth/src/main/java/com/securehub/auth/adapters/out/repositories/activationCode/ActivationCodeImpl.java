package com.securehub.auth.adapters.out.repositories.activationCode;

import com.securehub.auth.adapters.out.entities.JpaActivationCodeEntity;
import com.securehub.auth.domain.activationCode.ActivationCode;
import com.securehub.auth.domain.activationCode.ActivationCodeRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ActivationCodeImpl implements ActivationCodeRepositoryPort {
    private final JpaActivationCodeRepository activationCodeRepository;

    public ActivationCodeImpl(JpaActivationCodeRepository activationCodeRepository) {
        this.activationCodeRepository = activationCodeRepository;
    }

    @Override
    public ActivationCode save(ActivationCode activationCode) {
        JpaActivationCodeEntity entity = new JpaActivationCodeEntity(activationCode);
        JpaActivationCodeEntity activationCodeEntity = activationCodeRepository.save(entity);
        return toDomain(activationCodeEntity);
    }

    @Override
    public Optional<ActivationCode> findByCode(String code) {
        return activationCodeRepository.findByCode(code)
                .map(this::toDomain);
    }

    @Override
    public Optional<ActivationCode> findByUserId(String userId) {
        return activationCodeRepository.findByUserId(userId)
                .map(this::toDomain);
    }

    @Override
    public Optional<ActivationCode> findByUserIdAndCodeAndConfirmedAtIsNull(String userId, String code) {
        return activationCodeRepository.findByUserIdAndCodeAndConfirmedAtIsNull(userId, code)
                .map(this::toDomain);
    }

    private ActivationCode toDomain(JpaActivationCodeEntity entity) {
        return new ActivationCode(
            entity.getId(),
            entity.getUserId(),
            entity.getCode(),
            entity.getExpiresAt(),
            entity.getConfirmedAt()
        );
    }
}
