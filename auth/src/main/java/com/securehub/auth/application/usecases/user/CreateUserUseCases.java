package com.securehub.auth.application.usecases.user;

import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserDTO;

public interface CreateUserUseCases {
    UserDTO run(User user, String baseUrl);
}
