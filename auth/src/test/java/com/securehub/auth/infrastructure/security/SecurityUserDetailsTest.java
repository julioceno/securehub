package com.securehub.auth.infrastructure.security;

import com.securehub.auth.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUserDetailsTest {

    private User user;
    private SecurityUserDetails userDetails;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("hashedPassword");
        user.setEnabled(true);

        userDetails = new SecurityUserDetails(user);
    }

    @Test
    void shouldReturnEmptyAuthorities() {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void shouldReturnUserPassword() {
        String password = userDetails.getPassword();

        assertEquals("hashedPassword", password);
        assertEquals(user.getPassword(), password);
    }

    @Test
    void shouldReturnUsername() {
        String username = userDetails.getUsername();

        assertEquals("testuser", username);
        assertEquals(user.getUsername(), username);
    }

    @Test
    void shouldReturnAccountNonExpiredAsTrue() {
        boolean isAccountNonExpired = userDetails.isAccountNonExpired();

        assertTrue(isAccountNonExpired);
    }

    @Test
    void shouldReturnAccountNonLockedAsTrue() {
        boolean isAccountNonLocked = userDetails.isAccountNonLocked();

        assertTrue(isAccountNonLocked);
    }

    @Test
    void shouldReturnCredentialsNonExpiredAsTrue() {
        boolean isCredentialsNonExpired = userDetails.isCredentialsNonExpired();

        assertTrue(isCredentialsNonExpired);
    }

    @Test
    void shouldReturnEnabledStatusFromUser() {
        boolean isEnabled = userDetails.isEnabled();

        assertTrue(isEnabled);
        assertEquals(user.getEnabled(), isEnabled);
    }

    @Test
    void shouldReturnFalseWhenUserIsDisabled() {
        user.setEnabled(false);
        SecurityUserDetails disabledUserDetails = new SecurityUserDetails(user);

        boolean isEnabled = disabledUserDetails.isEnabled();

        assertFalse(isEnabled);
        assertEquals(user.getEnabled(), isEnabled);
    }

    @Test
    void shouldHandleNullPassword() {
        user.setPassword(null);
        SecurityUserDetails userDetailsWithNullPassword = new SecurityUserDetails(user);

        String password = userDetailsWithNullPassword.getPassword();

        assertNull(password);
    }

    @Test
    void shouldHandleNullUsername() {
        user.setUsername(null);
        SecurityUserDetails userDetailsWithNullUsername = new SecurityUserDetails(user);

        String username = userDetailsWithNullUsername.getUsername();

        assertNull(username);
    }
}