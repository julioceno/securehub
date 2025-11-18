package com.securehub.auth.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.securehub.auth.application.port.out.TokenProviderPort;
import com.securehub.auth.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.time.Instant;

@Component
public class JwtTokenProvider implements TokenProviderPort {
    private final String secretKey;
    private final long expirationInSeconds;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expirationInSeconds}") long expirationInSeconds
    ) {
        this.secretKey = secretKey;
        this.expirationInSeconds = expirationInSeconds;
    }

    @Override
    public String createToken(User user) {
        // TODO: criar um erro de autorização para quando não for possível gerar o token
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        Instant expiresAt = Instant
                .now()
                .plusSeconds(expirationInSeconds);

        return JWT.create()
                .withIssuer("secure-hub") // TODO: adicionar no application.yml
                .withExpiresAt(expiresAt)
                .withSubject(user.getEmail())
                .sign(algorithm);
    }

    @Override
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);

            var subject = JWT.require(algorithm)
                    .withIssuer("secure-hub") // TODO: adicionar no application.yml
                    .build()
                    .verify(token)
                    .getSubject();;

            return subject;
        } catch (JWTVerificationException exception) {
            exception.printStackTrace();
            return null;
        }
    }

}
