package com.securehub.auth.application.mapper;

import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void shouldMapUserToUserDTOSuccessfully() {
        User user = new User("userId", "username", "test@example.com", "password", true);

        UserDTO result = userMapper.toDto(user);

        assertNotNull(result);
        assertEquals("userId", result.id());
        assertEquals("username", result.username());
        assertEquals("test@example.com", result.email());
    }

    @Test
    void shouldMapUserToUserDTO_When_UserDisabled() {
        User user = new User("userId2", "disabledUser", "disabled@example.com", "password", false);

        UserDTO result = userMapper.toDto(user);

        assertNotNull(result);
        assertEquals("userId2", result.id());
        assertEquals("disabledUser", result.username());
        assertEquals("disabled@example.com", result.email());
    }

    @Test
    void shouldReturnNull_When_UserIsNull() {
        UserDTO result = userMapper.toDto(null);

        assertNull(result);
    }

    @Test
    void shouldMapAllFieldsCorrectly() {
        User user = new User("testId", "testUsername", "test@email.com", "hashedPassword", true);

        UserDTO result = userMapper.toDto(user);

        assertEquals(user.getId(), result.id());
        assertEquals(user.getUsername(), result.username());
        assertEquals(user.getEmail(), result.email());
    }
}