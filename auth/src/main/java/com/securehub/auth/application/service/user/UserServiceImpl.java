package com.securehub.auth.application.service.user;

import com.securehub.auth.application.usecases.user.ActivateUserUseCase;
import com.securehub.auth.application.usecases.user.CreateUserUseCases;
import com.securehub.auth.application.usecases.user.UserUseCases;
import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserDTO;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserUseCases {
    private final CreateUserUseCases createUserUseCase;
    private final ActivateUserUseCase activateUserUseCase;

    public UserServiceImpl(CreateUserUseCases createUserUseCase, ActivateUserUseCase activateUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.activateUserUseCase = activateUserUseCase;
    }

    @Override
    public UserDTO createUser(User user) {
        return createUserUseCase.run(user);
    }

    @Override
    public UserDTO enableUser(String email, String code) {
        return activateUserUseCase.run(email, code);
    }
}
