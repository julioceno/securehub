package com.securehub.auth.adapters.out.entities;

import com.securehub.auth.domain.activationCode.ActivationCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "activation_codes")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JpaActivationCodeEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private String id;

    private String userId;
    private String code;

    @Column(name = "expires_at")
    private Date expiresAt;

    @Column(name = "confirmed_at")
    private Date confirmedAt;

    public JpaActivationCodeEntity(ActivationCode activationCode) {
        this.id = activationCode.getId();
        this.userId = activationCode.getUserId();
        this.code = activationCode.getCode();
        this.expiresAt = activationCode.getExpiresAt();
        this.confirmedAt = activationCode.getConfirmedAt();
    }
}
