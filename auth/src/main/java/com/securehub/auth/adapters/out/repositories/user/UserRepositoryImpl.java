package com.securehub.auth.adapters.out.repositories.user;

import com.securehub.auth.adapters.out.entities.JpaUserEntity;
import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepositoryPort {
    private final JpaUserRepository jpaUserRepository;

    public UserRepositoryImpl(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public User save(User user) {
        JpaUserEntity entity = new JpaUserEntity(user);
        JpaUserEntity userCreated = jpaUserRepository.save(entity);
        return toDomain(userCreated);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(this::toDomain);
    }

    private User toDomain(JpaUserEntity jpaUserEntity) {
        return new User(
                jpaUserEntity.getId(),
                jpaUserEntity.getUsername(),
                jpaUserEntity.getEmail(),
                jpaUserEntity.getPassword(),
                jpaUserEntity.getEnabled()
        );
    }
}
