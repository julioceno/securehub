package com.securehub.auth.application.service.user;

import com.securehub.auth.application.exception.BadRequestException;
import com.securehub.auth.application.mapper.UserMapper;
import com.securehub.auth.application.port.out.PasswordHasher;
import com.securehub.auth.application.usecases.user.CreateActivateUserCodeUseCase;
import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserDTO;
import com.securehub.auth.domain.user.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceImplTest {
    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private CreateActivateUserCodeUseCase createActivateUserCodeUseCase;

    @InjectMocks
    private CreateUserServiceImpl createUserService;

    @Test
    void shouldCreateUserSuccessfully() {
        User userEntity = spy(new User(null, "username", "test@securehub.com", "password", false));

        when(passwordHasher.hash("password")).thenReturn("passwordHashed");
        when(userRepository.findByEmail("test@securehub.com")).thenReturn(Optional.empty());

        User savedUser = new User("Id", "username", "test@securehub.com", "passwordHashed", false);
        when(userRepository.save(userEntity)).thenReturn(savedUser);

        UserDTO expectedDto = new UserDTO("Id", "username", "test@securehub.com");
        when(userMapper.toDto(savedUser)).thenReturn(expectedDto);

        UserDTO result = createUserService.run(userEntity);

        verify(passwordHasher).hash("password");
        verify(userEntity).setPassword("passwordHashed");
        verify(userRepository).save(userEntity);
        verify(createActivateUserCodeUseCase).run(savedUser);
        assertEquals(expectedDto, result);
    }

    @Test
    void shouldThrowBadRequest_When_EmailAlreadyExists() {
        User userToCreate = new User(null, "username", "test@securehub.com", "password", false);

        User existing = new User("existingId", "other", "test@securehub.com", "xxx", true);
        when(userRepository.findByEmail("test@securehub.com")).thenReturn(Optional.of(existing));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> createUserService.run(userToCreate));

        assertEquals("User with email [test@securehub.com] already used", ex.getMessage());
        verify(userRepository).findByEmail("test@securehub.com");
        verify(userRepository, never()).save(any());
        verify(passwordHasher, never()).hash(anyString());
        verify(userMapper, never()).toDto(any());
        verify(createActivateUserCodeUseCase, never()).run(any(User.class));
    }
}