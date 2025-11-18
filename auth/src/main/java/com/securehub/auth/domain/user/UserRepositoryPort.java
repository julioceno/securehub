package com.securehub.auth.domain.user;

import java.util.Optional;

public interface UserRepositoryPort {

    User save (User user);

    Optional<User> findByEmail (String email);
}
