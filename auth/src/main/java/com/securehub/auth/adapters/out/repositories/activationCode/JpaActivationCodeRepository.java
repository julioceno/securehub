package com.securehub.auth.adapters.out.repositories.activationCode;

import com.securehub.auth.adapters.out.entities.JpaActivationCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaActivationCodeRepository extends JpaRepository<JpaActivationCodeEntity, String> {
    Optional<JpaActivationCodeEntity> findByCode(String code);

    Optional<JpaActivationCodeEntity> findByUserId(String userId);

    Optional<JpaActivationCodeEntity> findByUserIdAndCodeAndConfirmedAtIsNull(String userId, String code);

}
