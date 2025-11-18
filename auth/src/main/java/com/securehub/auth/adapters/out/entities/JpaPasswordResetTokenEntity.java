package com.securehub.auth.adapters.out.entities;

import com.securehub.auth.domain.passwordResetToken.PasswordResetToken;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "password_reset_tokens")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JpaPasswordResetTokenEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private String id;
    private String userId;
    private String token;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    public JpaPasswordResetTokenEntity(PasswordResetToken passwordResetToken) {
        this.id = passwordResetToken.getId();
        this.userId = passwordResetToken.getUserId();
        this.token = passwordResetToken.getToken();
        this.expiresAt = passwordResetToken.getExpiresAt();
        this.confirmedAt = passwordResetToken.getConfirmedAt();
    }
}
