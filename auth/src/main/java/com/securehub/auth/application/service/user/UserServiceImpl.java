package com.securehub.auth.application.service.user;

import com.securehub.auth.application.usecases.user.CreateUserUseCases;
import com.securehub.auth.application.usecases.user.UserUseCases;
import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserDTO;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserUseCases {
    private final CreateUserUseCases createUserService;

    public UserServiceImpl(CreateUserUseCases createUserService) {
        this.createUserService = createUserService;
    }

    @Override
    public UserDTO createUser(User user) {
        return createUserService.run(user);
    }

    @Override
    public UserDTO findById(String id) {
        return null;
    }

    @Override
    public void deleteById(String id) {

    }
}
