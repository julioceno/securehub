package com.securehub.auth.adapters.in.controller;

import com.securehub.auth.adapters.in.dto.UserToCreateDTO;
import com.securehub.auth.domain.user.User;
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

    public UsersController(UserUseCases userUseCases) {
        this.userUseCases = userUseCases;
    }

    @PostMapping()
    public ResponseEntity<UserDTO> createUser(
           @Valid @RequestBody UserToCreateDTO body
    ) {
        User user = new User(null,  body.getUsername(), body.getEmail(), body.getPassword());
        UserDTO userDTO = userUseCases.createUser(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userDTO.id())
                .toUri();

        return ResponseEntity.created(location).body(userDTO);
    }
}
