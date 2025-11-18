package com.securehub.auth.domain.activationCode;

import java.time.Instant;
import java.util.Date;

public class ActivationCode {
    private String id;
    private String userId;
    private String code;
    private Instant expiresAt;
    private Instant confirmedAt;

    public ActivationCode() {
    }

    public ActivationCode(String id, String userId, String code, Instant expiresAt, Instant confirmedAt) {
        this.id = id;
        this.userId = userId;
        this.code = code;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
