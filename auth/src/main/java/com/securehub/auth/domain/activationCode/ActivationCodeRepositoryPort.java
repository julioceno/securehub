package com.securehub.auth.domain.activationCode;

import java.util.Optional;

public interface ActivationCodeRepositoryPort {

    ActivationCode save(ActivationCode activationCode);

    Optional<ActivationCode> findByCode(String code);

    Optional<ActivationCode> findByUserId(String userId);
}
