package com.securehub.auth.adapters.out.repositories.user;

import com.securehub.auth.adapters.out.entities.JpaUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<JpaUserEntity, String> {
    Optional<JpaUserEntity> findByEmail(String email);

    Optional<JpaUserEntity> findById(String id);
}
