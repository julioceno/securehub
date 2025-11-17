package com.securehub.auth.infrastructure.security;

import com.securehub.auth.domain.user.UserRepositoryPort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceAdapter implements UserDetailsService {
    private final UserRepositoryPort userRepository;

    public UserDetailsServiceAdapter(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(SecurityUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User not found: %s", email)));
    }
}
