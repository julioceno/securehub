package com.securehub.auth.domain.passwordResetToken;

import java.time.Instant;

public class PasswordResetToken {
    private String id;
    private String userId;
    private String token;
    private Instant expiresAt;
    private Instant confirmedAt;

    public PasswordResetToken() {
    }

    public PasswordResetToken(String id, String userId, String token, Instant expiresAt, Instant confirmedAt) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.expiresAt = expiresAt;
        this.confirmedAt = confirmedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
}
