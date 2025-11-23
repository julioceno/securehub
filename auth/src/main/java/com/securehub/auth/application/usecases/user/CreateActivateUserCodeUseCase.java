package com.securehub.auth.application.usecases.user;

import com.securehub.auth.domain.user.User;

public interface CreateActivateUserCodeUseCase {
    void run(User user);
}
