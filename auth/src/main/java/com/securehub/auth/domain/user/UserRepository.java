package com.securehub.auth.domain.user;

import java.util.Optional;

public interface UserRepository {

    User save (User user);

    User findById (String id);

    Optional<User> findByEmail (String email);

    void deleteById (String id);
}
