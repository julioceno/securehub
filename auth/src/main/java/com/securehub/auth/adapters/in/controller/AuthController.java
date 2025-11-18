package com.securehub.auth.adapters.in.controller;

import com.securehub.auth.adapters.in.dto.SignInDTO;
import com.securehub.auth.application.dto.AuthRequest;
import com.securehub.auth.application.dto.AuthResponse;
import com.securehub.auth.application.usecases.auth.AuthenticateUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/auth")
public class AuthController {
    private final AuthenticateUserUseCase authenticateUserUseCase;

    public AuthController(AuthenticateUserUseCase authenticateUserUseCase) {
        this.authenticateUserUseCase = authenticateUserUseCase;
    }

    @PostMapping()
    public ResponseEntity<AuthResponse> createUser(
            @Valid @RequestBody SignInDTO body
    ) {
        AuthRequest authRequest = new AuthRequest(body.email(), body.password());
        AuthResponse authResponse = authenticateUserUseCase.authenticate(authRequest);

        return ResponseEntity.ok().body(authResponse);
    }

    @GetMapping("me")
    public ResponseEntity<String> me() {
        return ResponseEntity.ok().body("oioi");
    }
}
