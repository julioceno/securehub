package com.securehub.auth.adapters.in.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PingControllerTest {

    @InjectMocks
    private PingController pingController;

    @Test
    void ping_ShouldReturnOkWithPongMessage() {
        ResponseEntity<String> response = pingController.ping();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("pong", response.getBody());
    }
}