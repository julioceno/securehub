package com.securehub.auth.adapters.out.repositories;

import com.securehub.auth.adapters.out.entities.JpaUserEntity;
import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;

    public UserRepositoryImpl(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public User save(User user) {
        JpaUserEntity entity = new JpaUserEntity(user);
        JpaUserEntity userCreated = jpaUserRepository.save(entity);

        return new User(
                userCreated.getId(),
                userCreated.getUsername(),
                userCreated.getEmail(),
                userCreated.getPassword()
        );
    }

    @Override
    public User findById(String id) {
        return null;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(user -> new User(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getPassword()
                ));
    }

    @Override
    public void deleteById(String id) {

    }
}
