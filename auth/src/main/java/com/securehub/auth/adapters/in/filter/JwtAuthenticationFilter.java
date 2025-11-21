package com.securehub.auth.adapters.in.filter;

import com.securehub.auth.application.util.CorrelationId;
import com.securehub.auth.infrastructure.security.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String tokenCookieName = "token";
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String correlationId = CorrelationId.get();
        log.debug("JwtAuthenticationFilter.doFilterInternal - start - correlationId [{}] - path [{}] - method [{}]",
                correlationId, request.getRequestURI(), request.getMethod());

        String email = getEmail(request);
        if (StringUtils.hasText(email)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.debug("JwtAuthenticationFilter.doFilterInternal - authenticated - correlationId [{}] - email [{}]", correlationId, email);
        } else {
            log.debug("JwtAuthenticationFilter.doFilterInternal - no authentication - correlationId [{}] - path [{}]", correlationId, request.getRequestURI());
        }

        filterChain.doFilter(request, response);

        log.debug("JwtAuthenticationFilter.doFilterInternal - end - correlationId [{}] - path [{}] - status [{}]",
                correlationId, request.getRequestURI(), response.getStatus());
    }

    private String getEmail(HttpServletRequest request) {
        String correlationId = CorrelationId.get();
        try {
            String token = getToken(request);
            if  (!StringUtils.hasText(token)) {
                log.debug("JwtAuthenticationFilter.getEmail - no token found - correlationId [{}] - path [{}]", correlationId, request.getRequestURI());
                return null;
            }

            String email = jwtTokenProvider.validateToken(token);
            log.debug("JwtAuthenticationFilter.getEmail - token validated - correlationId [{}] - email [{}]", correlationId, email);
            return email;
        } catch (Exception err) {
            log.error("JwtAuthenticationFilter.getEmail - token validation error - correlationId [{}] - error [{}]", correlationId, err.getMessage());
            return null;
        }
    }

    private String getToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            log.debug("JwtAuthenticationFilter.getToken - no cookies - correlationId [{}] - path [{}]", CorrelationId.get(), request.getRequestURI());
            return null;
        }

        Optional<Cookie> token = Arrays.stream(request.getCookies())
                .filter(item -> tokenCookieName.equals(item.getName()))
                .findFirst();

        if (token.isPresent()) {
            log.debug("JwtAuthenticationFilter.getToken - token cookie found - correlationId [{}] - path [{}]", CorrelationId.get(), request.getRequestURI());
        } else {
            log.debug("JwtAuthenticationFilter.getToken - token cookie not found - correlationId [{}] - path [{}]", CorrelationId.get(), request.getRequestURI());
        }

        return token.map(Cookie::getValue).orElse(null);
    }
}
