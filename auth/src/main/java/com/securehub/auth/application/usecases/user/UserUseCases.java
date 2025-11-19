package com.securehub.auth.application.usecases.user;

import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserDTO;

public interface UserUseCases {

    UserDTO createUser(User user);

    void enableUser(String email, String code);

    void forgotPassword(String email);

    void resetPassword(String userId, String token, String password);
}
