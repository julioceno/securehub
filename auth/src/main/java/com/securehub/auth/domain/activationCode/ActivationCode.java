package com.securehub.auth.domain.activationCode;

import java.util.Date;

public class ActivationCode {
    private String id;
    private String userId;
    private String code;
    private Date expiresAt;
    private Date confirmedAt;

    public ActivationCode() {
    }

    public ActivationCode(String id, String userId, String code, Date expiresAt, Date confirmedAt) {
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

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Date getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Date confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
}
