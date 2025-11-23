package com.securehub.auth.adapters.in.controller;

import com.securehub.auth.adapters.in.dto.SignInDTO;
import com.securehub.auth.application.dto.AuthRequestDTO;
import com.securehub.auth.application.dto.AuthResponse;
import com.securehub.auth.application.usecases.auth.AuthenticateUserUseCase;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    private final HttpServletRequest request;
    private final HttpServletResponse httpResponse;

    @PostMapping
    public ResponseEntity createUser(
            @Valid @RequestBody SignInDTO body
    ) {
        String baseUrl = request.getRequestURL()
                .toString()
                .replace(request.getRequestURI(), "");

        AuthRequestDTO authRequest = new AuthRequestDTO(body.email(), body.password(), baseUrl);
        AuthResponse authResponse = authenticateUserUseCase.run(authRequest);

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
