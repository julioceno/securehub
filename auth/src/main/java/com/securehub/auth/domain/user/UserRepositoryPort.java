package com.securehub.auth.domain.user;

import java.util.Optional;

public interface UserRepositoryPort {

    User save (User user);

    User findById (String id);

    Optional<User> findByEmail (String email);

    void deleteById (String id);
}
