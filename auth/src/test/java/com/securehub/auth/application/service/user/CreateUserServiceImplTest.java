package com.securehub.auth.application.service.user;

import com.securehub.auth.application.exception.BadRequestException;
import com.securehub.auth.application.mapper.UserMapper;
import com.securehub.auth.application.port.out.PasswordHasher;
import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserDTO;
import com.securehub.auth.domain.user.UserRepository;
import com.securehub.auth.domain.user.UserToCreateDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private CreateUserServiceImpl createUserService;

    @Test
    void shouldCreateUserSuccessfully() {
        UserToCreateDTO toCreateDTO = new UserToCreateDTO("username", "test@securehub.com", "password");

        User userEntity = mock(User.class);
        when(userMapper.toEntityFromCreateDTO(toCreateDTO)).thenReturn(userEntity);
        when(userEntity.getPassword()).thenReturn("password");

        when(passwordHasher.hash("password")).thenReturn("passwordHashed");

        when(userRepository.findByEmail("test@securehub.com")).thenReturn(Optional.empty());
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        UserDTO expectedDto = new UserDTO("Id", "username", "test@securehub.com");
        when(userMapper.toDto(userEntity)).thenReturn(expectedDto);

        UserDTO result = createUserService.run(toCreateDTO);

        verify(passwordHasher).hash("password");
        verify(userEntity).setPassword("passwordHashed");
        verify(userRepository).save(userEntity);
        assertEquals(expectedDto, result);
    }

    @Test
    void shouldThrowBadRequest_When_EmailAlreadyExists() {
        UserToCreateDTO toCreateDTO = new UserToCreateDTO("username", "test@securehub.com", "password");
        User userEntity = mock(User.class);
        when(userRepository.findByEmail("test@securehub.com")).thenReturn(Optional.of(userEntity));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> createUserService.run(toCreateDTO));

        assertEquals("User with email [test@securehub.com] already used", ex.getMessage());
        verify(userRepository).findByEmail("test@securehub.com");
        verify(userRepository, never()).save(userEntity);
        verify(passwordHasher, never()).hash(anyString());
        verify(userMapper, never()).toDto(any());
    }
}