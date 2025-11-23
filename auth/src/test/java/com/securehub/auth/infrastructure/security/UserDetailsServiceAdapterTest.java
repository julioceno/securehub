package com.securehub.auth.infrastructure.security;

import com.securehub.auth.domain.user.User;
import com.securehub.auth.domain.user.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceAdapterTest {

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private UserDetailsServiceAdapter userDetailsService;

    @Test
    void shouldLoadUserByUsername_When_UserExists() {
        String email = "user@example.com";
        User mockUser = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        UserDetails result = userDetailsService.loadUserByUsername(email);

        assertNotNull(result);
        assertInstanceOf(SecurityUserDetails.class, result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldThrowUsernameNotFoundException_When_UserDoesNotExist() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(email)
        );

        assertEquals("User not found: nonexistent@example.com", exception.getMessage());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldCallRepositoryWithCorrectEmail() {
        String email = "test@domain.com";
        User mockUser = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        userDetailsService.loadUserByUsername(email);

        verify(userRepository, times(1)).findByEmail(email);
    }
}