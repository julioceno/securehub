package com.securehub.auth.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.securehub.auth.application.exception.UnauthorizedException;
import com.securehub.auth.application.port.out.TokenProviderPort;
import com.securehub.auth.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.time.Instant;

@Component
public class JwtTokenProvider implements TokenProviderPort {
    private final String secretKey;
    private final long expirationInSeconds;
    private final String issuer;

    public JwtTokenProvider(
            @Value("${api.jwt.secret}") String secretKey,
            @Value("${api.jwt.expirationInSeconds}") long expirationInSeconds,
            @Value("${api.jwt.issuer}") String issuer
    ) {
        this.secretKey = secretKey;
        this.expirationInSeconds = expirationInSeconds;
        this.issuer = issuer;
    }

    @Override
    public String createToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            Instant expiresAt = Instant
                    .now()
                    .plusSeconds(expirationInSeconds);

            return JWT.create()
                    .withIssuer(issuer)
                    .withExpiresAt(expiresAt)
                    .withSubject(user.getEmail())
                    .sign(algorithm);
        } catch (Exception e) {
            throw new UnauthorizedException("Is impossible to generate JWT Token");
        }
    }

    @Override
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);

            return JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

}
