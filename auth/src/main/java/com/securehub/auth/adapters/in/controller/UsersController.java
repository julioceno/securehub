package com.securehub.auth.adapters.in.controller;

import com.securehub.auth.adapters.in.dto.EnableUserDTO;
import com.securehub.auth.adapters.in.dto.ForgotPasswordDTO;
import com.securehub.auth.adapters.in.dto.ResetPasswordDTO;
import com.securehub.auth.adapters.in.dto.UserToCreateDTO;
import com.securehub.auth.domain.passwordResetToken.RequestPasswordResetTokenDTO;
import com.securehub.auth.domain.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import com.securehub.auth.application.usecases.user.UserUseCases;
import com.securehub.auth.domain.user.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("v1/users")
public class UsersController {
    private final UserUseCases userUseCases;
    private final HttpServletRequest request;

    public UsersController(UserUseCases userUseCases, HttpServletRequest request) {
        this.userUseCases = userUseCases;
        this.request = request;
    }

    @PostMapping()
    public ResponseEntity<UserDTO> createUser(
           @RequestBody @Valid UserToCreateDTO body
    ) {
        User user = new User(
                null,
                body.getUsername(),
                body.getEmail(),
                body.getPassword(),
                false
        );
        String baseUrl = request.getRequestURL()
                .toString()
                .replace(request.getRequestURI(), "");

        UserDTO userDTO = userUseCases.createUser(user, baseUrl);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userDTO.id())
                .toUri();

        return ResponseEntity.created(location).body(userDTO);
    }

    @PostMapping("/enable")
    public ResponseEntity<UserDTO> createActiveUser(
            @Valid @RequestBody EnableUserDTO body
    ) {
        userUseCases.enableUser(body.email(), body.code());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/forgot")
    public ResponseEntity forgotPassword(
            @Valid @RequestBody ForgotPasswordDTO body
    ) {
        userUseCases.forgotPassword(body.email());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Void> reset(@RequestBody ResetPasswordDTO body) {
        RequestPasswordResetTokenDTO dto = new RequestPasswordResetTokenDTO(body.email(), body.token(), body.password());
        userUseCases.resetPassword(dto);
        return ResponseEntity.noContent().build();
    }

}
