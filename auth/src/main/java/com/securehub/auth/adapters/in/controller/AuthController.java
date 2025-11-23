package com.securehub.auth.adapters.in.controller;

import com.securehub.auth.adapters.in.dto.SignInRequestDTO;
import com.securehub.auth.application.dto.SignInDTO;
import com.securehub.auth.application.dto.AuthResponse;
import com.securehub.auth.application.usecases.auth.AuthenticateUserUseCase;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final HttpServletResponse httpResponse;

    @PostMapping
    public ResponseEntity<?> createUser(
            @Valid @RequestBody SignInRequestDTO body
    ) {
        AuthResponse authResponse = authenticateUserUseCase.run(body.toDomain());

        Cookie cookie = new Cookie("token", authResponse.token());
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        httpResponse.addCookie(cookie);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("me")
    public ResponseEntity<String> me() {
        return ResponseEntity.ok().body("oioi");
    }
}
