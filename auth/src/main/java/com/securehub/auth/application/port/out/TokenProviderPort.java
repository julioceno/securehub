package com.securehub.auth.application.port.out;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.securehub.auth.domain.user.User;

public interface TokenProviderPort {
    String createToken(User user);
    String validateToken(String token);
}
