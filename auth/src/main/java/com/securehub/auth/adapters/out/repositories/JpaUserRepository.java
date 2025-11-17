package com.securehub.auth.adapters.out.repositories;

import com.securehub.auth.adapters.out.entities.JpaUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<JpaUserEntity, String> {
    Optional<JpaUserEntity> findByEmail(String email);
}
