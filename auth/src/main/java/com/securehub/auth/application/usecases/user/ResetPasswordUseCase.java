package com.securehub.auth.application.usecases.user;

public interface ResetPasswordUseCase {
    void run(String userId, String rawToken, String newPlainPassword);
}