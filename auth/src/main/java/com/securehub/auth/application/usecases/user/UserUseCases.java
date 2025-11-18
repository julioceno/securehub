package com.securehub.auth.application.usecases.user;

import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserDTO;

public interface UserUseCases {

    public UserDTO createUser(User user);

    public UserDTO enableUser(String email, String code);
}
