package com.securehub.auth.domain.activationCode;

import java.util.Optional;

public interface ActivationCodeRepositoryPort {

    ActivationCode save(ActivationCode activationCode);

    Optional<ActivationCode> findByUserIdAndCodeAndConfirmedAtIsNull(String userId, String code);
}
