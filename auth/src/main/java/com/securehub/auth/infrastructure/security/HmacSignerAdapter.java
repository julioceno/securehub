package com.securehub.auth.infrastructure.security;

import com.securehub.auth.application.port.out.SignerPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class HmacSignerAdapter implements SignerPort {
    private static final String ALGORITHM = "HmacSHA256";
    private final String secretKey;

    public HmacSignerAdapter(
            @Value("${api.encrypt.secret}")
            String secretKey
    ) {
        this.secretKey = secretKey;
    }

    public String encrypt(String token) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(ALGORITHM);

        SecretKeySpec keySpec = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8),
                ALGORITHM
        );

        mac.init(keySpec);
        byte[] hmacBytes = mac.doFinal(token.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hmacBytes);
    }

    public boolean compare(
            String rawToken,
            String storedHmac
    ) {
        if (rawToken == null || secretKey == null || storedHmac == null) {
            return false;
        }

        try {
            String newHmac = encrypt(rawToken);
            byte[] a = HexFormat.of().parseHex(newHmac);
            byte[] b = HexFormat.of().parseHex(storedHmac);
            return MessageDigest.isEqual(a, b);
        } catch (Exception e) {
            return false;
        }
    }
}
