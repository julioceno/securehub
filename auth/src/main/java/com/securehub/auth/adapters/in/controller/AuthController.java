package com.securehub.auth.adapters.in.controller;

import com.securehub.auth.adapters.in.dto.SignInDTO;
import com.securehub.auth.application.dto.AuthRequest;
import com.securehub.auth.application.dto.AuthResponse;
import com.securehub.auth.application.usecases.auth.AuthenticateUserUseCase;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/auth")
public class AuthController {
    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final HttpServletResponse httpResponse;

    public AuthController(AuthenticateUserUseCase authenticateUserUseCase, HttpServletResponse httpResponse) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.httpResponse = httpResponse;
    }

    @PostMapping()
    public ResponseEntity createUser(
            @Valid @RequestBody SignInDTO body
    ) {
        AuthRequest authRequest = new AuthRequest(body.email(), body.password());
        AuthResponse authResponse = authenticateUserUseCase.authenticate(authRequest);

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
